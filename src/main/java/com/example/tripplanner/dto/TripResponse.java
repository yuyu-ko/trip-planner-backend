package com.example.tripplanner.dto;

import com.example.tripplanner.model.Trip;

import java.util.ArrayList;
import java.util.List;

public record TripResponse(
        String id,
        String city,
        String startDate,
        String endDate,
        String budgetLevel,
        List<String> preferences,
        String status,
        List<DayPlanResponse> dayPlans
) {
    public static TripResponse fromEntity(Trip t) {
        return new TripResponse(
                t.getId(),
                t.getCity(),
                // 如果日期是 null，回傳空字串或 null，不要叫 toString()
                t.getStartDate() != null ? t.getStartDate().toString() : "",
                t.getEndDate() != null ? t.getEndDate().toString() : "",
                t.getBudgetLevel() != null ? t.getBudgetLevel().name() : "MEDIUM",
                t.getPreferences() != null ? t.getPreferences() : new ArrayList<>(),
                t.getStatus() != null ? t.getStatus().name() : "PENDING",
                t.getDayPlans() != null ? t.getDayPlans().stream()
                        .map(DayPlanResponse::fromEntity)
                        .toList() : new ArrayList<>()
        );
    }
}

