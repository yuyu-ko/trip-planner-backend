package com.example.tripplanner.controller;

import com.example.tripplanner.dto.CreateTripRequest;
import com.example.tripplanner.dto.TripResponse;
import com.example.tripplanner.dto.UpdateTripRequest;
import com.example.tripplanner.model.Trip;
import com.example.tripplanner.service.PDFService;
import com.example.tripplanner.service.TripService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TripController {

    private final TripService tripService;
    private final PDFService pdfService;

    @PostMapping
    public TripResponse createTrip(@RequestBody CreateTripRequest req) {
        return tripService.createTrip(req);
    }

    @GetMapping("/{id}")
    public TripResponse getTrip(@PathVariable("id") String tripId) {
        return tripService.getTrip(tripId);
    }

    @GetMapping
    public List<TripResponse> getAllTrips(HttpServletRequest request) {
        return tripService.getAllTrips();
    }

    @PutMapping("/{id}")
    public TripResponse updateTrip(
            @PathVariable("id") String id,
            @RequestBody UpdateTripRequest request
    ) {
        return tripService.updateTrip(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable("id") String id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/generate")
    public TripResponse generateAIItinerary(@PathVariable("id") String id) throws JsonProcessingException {
        return tripService.generateAIItinerary(id).block();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable("id")  String id) {
        Trip trip = tripService.getTripEntity(id);  // 需要提供能取得 entity 的方法

        byte[] pdfBytes = pdfService.generateTripPDF(trip);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trip-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
