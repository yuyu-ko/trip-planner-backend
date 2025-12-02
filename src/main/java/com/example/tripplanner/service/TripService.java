package com.example.tripplanner.service;

import com.example.tripplanner.config.UserContext;
import com.example.tripplanner.dto.*;
import com.example.tripplanner.exception.ResourceNotFoundException;
import com.example.tripplanner.model.*;
import com.example.tripplanner.repository.TripRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    @Autowired
    private AIService aiService;
    @Autowired
    private GooglePlacesService googlePlacesService;

    public TripResponse createTrip(CreateTripRequest req) {
        String currentUserId = UserContext.currentUser.get();
        if (currentUserId == null) {
            throw new RuntimeException("User not logged in");
        }
        Trip trip = Trip.builder()
                .city(req.city())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .budgetLevel(req.budgetLevel())
                .preferences(req.preferences())
                .status(TripStatus.PENDING)
                .dayPlans(new ArrayList<>())
                .userId(currentUserId)
                .build();

        tripRepository.save(trip);

        // TODO: 呼叫 FastAPI AI Service
        // TODO: 解析 AI 回覆後更新 Trip 與 DayPlan

        trip.setStatus(TripStatus.PENDING);

        tripRepository.save(trip);

        return TripResponse.fromEntity(trip);
    }

    public TripResponse getTrip(@PathVariable("id") String id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + id));
        // 🔒 安全檢查：確認這筆行程是屬於當前使用者的
        validateOwnership(trip);
        return TripResponse.fromEntity(trip);
    }


    // ----------------------------------------------------------------
    // 1. 查詢全部 (只回傳自己的)
    // ----------------------------------------------------------------
    public List<TripResponse> getAllTrips() {
        String currentUserId = UserContext.currentUser.get();

        // 🔒 修正：呼叫 findAllByUserId
        // 現在這裡回傳的是 List<Trip>
        return tripRepository.findAllByUserId(currentUserId)
                .stream()  // 這裡變成 Stream<Trip>
                .map(TripResponse::fromEntity)
                .toList();
    }

    public TripResponse updateTrip(@PathVariable("id") String id,
                                   @RequestBody UpdateTripRequest req) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + id));
        // 🔒 安全檢查
        validateOwnership(trip);
        boolean changed = false;

        if (req.getCity() != null && !req.getCity().equals(trip.getCity())) {
            trip.setCity(req.getCity());
            changed = true;
        }

        if (req.getStartDate() != null && !req.getStartDate().equals(trip.getStartDate())) {
            trip.setStartDate(req.getStartDate());
            changed = true;
        }

        if (req.getEndDate() != null && !req.getEndDate().equals(trip.getEndDate())) {
            trip.setEndDate(req.getEndDate());
            changed = true;
        }

        if (req.getBudgetLevel() != null && req.getBudgetLevel() != trip.getBudgetLevel()) {
            trip.setBudgetLevel(req.getBudgetLevel());
            changed = true;
        }

        if (req.getPreferences() != null) {
            trip.setPreferences(req.getPreferences());
            changed = true;
        }

        // 🧹 若資料有變 → 清空 DayPlans & 改狀態
        if (changed) {
            trip.getDayPlans().clear();
            trip.setStatus(TripStatus.PENDING);
        }

        Trip saved = tripRepository.save(trip);
        return TripResponse.fromEntity(saved);
    }

    public void deleteTrip(String id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + id));
        // 🔒 安全檢查
        validateOwnership(trip);
        tripRepository.delete(trip);
    }

    public Mono<TripResponse> generateAIItinerary(String id) throws JsonProcessingException {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + id));
        // 🔒 安全檢查
        validateOwnership(trip);
        // 轉成 TripRequest（AI 所需格式）
        CreateTripRequest req = new CreateTripRequest(
                trip.getCity(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getBudgetLevel(),
                trip.getPreferences()
        );

        return aiService.generateItinerary(req)
                .doOnNext(ai -> {
                    System.out.println("=== AI RESPONSE ===");
                    System.out.println(ai);
                })
                .map(aiResponse -> {
                    Trip updatedTrip = saveAIItineraryToDB(trip, aiResponse);
                    updatedTrip.setStatus(TripStatus.READY);  // 🔥 AI 產生完成 → READY
                    return tripRepository.save(updatedTrip);})
                .doOnNext(tripSaved -> {
                    System.out.println("=== TRIP AFTER SAVE ===");
                    System.out.println(tripSaved);
                })
                .map(TripResponse::fromEntity);
    }

    private Trip saveAIItineraryToDB(Trip trip, ItineraryResponse ai) {

        // 清空舊資料（如果有）
        trip.getDayPlans().clear();

        for (DayPlanDTO dayDto : ai.getDays()) {

            DayPlan dayPlan = DayPlan.builder()
                    .dayNumber(dayDto.getDayNumber())
                    .date(LocalDate.parse(dayDto.getDate()))
                    .trip(trip)
                    .build();

            List<Activity> activities = new ArrayList<>();

            // 在 saveAIItineraryToDB 方法內...

            for (ActivityDTO actDto : dayDto.getActivities()) {

                // 🔴原本是這樣 (只用 location 查評分，容易找不到)：
                // Double rating = googlePlacesService.getPlaceRating(actDto.getLocation());

                // 🟢 修改後：建立一個更精準的搜尋字串 (名稱 + 地點)
                String placeQuery = actDto.getTitle() + " " + actDto.getLocation();

                // 用這個組合字串去查評分，Google 比較容易聽懂
                Double rating = googlePlacesService.getPlaceRating(placeQuery);

                // 圖片也是用同樣的 query (這行原本就有，保持不動)
                String imageUrl = googlePlacesService.getPlacePhotoUrl(placeQuery);

                Activity activity = Activity.builder()
                        .time(actDto.getTime())
                        .title(actDto.getTitle())
                        .description(actDto.getDescription())
                        .location(actDto.getLocation())
                        .googleRating(rating) // 存入更準確的評分
                        .imageUrl(imageUrl)
                        .dayPlan(dayPlan)
                        .build();

                activities.add(activity);
            }

            dayPlan.setActivities(activities);
            trip.getDayPlans().add(dayPlan);


        }

        return tripRepository.save(trip);
    }


    public Trip getTripEntity(String id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        validateOwnership(trip); // 🔒 PDF 下載也要檢查
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }

    // 🔥 抽取出共用的權限檢查邏輯
    private void validateOwnership(Trip trip) {
        String currentUserId = UserContext.currentUser.get();
        // 如果 trip.getUserId() 是 null (舊資料)，或者 ID 不匹配，就報錯
        if (trip.getUserId() == null || !trip.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: You do not own this trip");
        }
    }


}
