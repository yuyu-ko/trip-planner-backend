package com.example.tripplanner.dto;

import com.example.tripplanner.model.BudgetLevel;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UpdateTripRequest {
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private BudgetLevel budgetLevel;
    private List<String> preferences;
}
