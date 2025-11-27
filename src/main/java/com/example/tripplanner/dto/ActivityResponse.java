package com.example.tripplanner.dto;

import com.example.tripplanner.model.Activity;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityResponse {
    private String time;
    private String title;
    private String description;
    private String location;
    private Double googleRating;
    private String imageUrl;

    public static ActivityResponse fromEntity(Activity a) {
        return ActivityResponse.builder()
                .time(a.getTime())
                .title(a.getTitle())
                .description(a.getDescription())
                .location(a.getLocation())
                .googleRating(a.getGoogleRating())
                .imageUrl(a.getImageUrl())
                .build();
    }
}
