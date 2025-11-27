package com.example.tripplanner.dto;

import com.example.tripplanner.model.DayPlan;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DayPlanResponse {
    private int dayNumber;
    private String date;
    private List<ActivityResponse> activities;

    public static DayPlanResponse fromEntity(DayPlan d) {
        return DayPlanResponse.builder()
                .dayNumber(d.getDayNumber())
                .date(d.getDate().toString())
                .activities(
                        d.getActivities().stream()
                                .map(ActivityResponse::fromEntity)
                                .toList()
                )
                .build();
    }
}
