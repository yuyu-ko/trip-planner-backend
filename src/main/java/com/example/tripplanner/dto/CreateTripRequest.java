package com.example.tripplanner.dto;

import com.example.tripplanner.model.BudgetLevel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record CreateTripRequest(
        String city,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
        BudgetLevel budgetLevel,
        List<String> preferences
) {}
