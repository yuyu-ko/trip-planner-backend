package com.example.tripplanner.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DayPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private int dayNumber;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @OneToMany(mappedBy = "dayPlan", cascade = CascadeType.ALL)
    private List<Activity> activities = new ArrayList<>();
}
