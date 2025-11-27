package com.example.tripplanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String city;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private BudgetLevel budgetLevel;

    @ElementCollection
    @CollectionTable(name = "trip_preferences", joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "preference")
    private List<String> preferences = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DayPlan> dayPlans = new ArrayList<>();

    @Column(name = "user_id")
    private String userId;

}

