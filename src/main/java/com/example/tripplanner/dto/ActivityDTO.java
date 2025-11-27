package com.example.tripplanner.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private String time;
    private String title;
    private String description;
    private String location;
    private Double googleRating;
    private String imageUrl;
}
