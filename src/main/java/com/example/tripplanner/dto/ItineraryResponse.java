package com.example.tripplanner.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryResponse {
    private String tripId;   // optional
    private List<DayPlanDTO> days;
}
