package com.example.tripplanner.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String time;
    private String title;
    private String description;
    private String location;
    private Double googleRating;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;


    @ManyToOne
    @JoinColumn(name = "day_plan_id")
    private DayPlan dayPlan;
}
