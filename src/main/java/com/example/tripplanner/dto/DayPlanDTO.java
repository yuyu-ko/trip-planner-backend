package com.example.tripplanner.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayPlanDTO {
    private int dayNumber;
    private String date;
    private List<ActivityDTO> activities;
}
