package com.example.tripplanner.controller;

import com.example.tripplanner.service.GooglePlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google")
@RequiredArgsConstructor
public class GoogleController {

    private final GooglePlacesService googlePlacesService;

    @GetMapping("/rating")
    public Double getRating(@RequestParam String address) {
        return googlePlacesService.getPlaceRating(address);
    }
}

