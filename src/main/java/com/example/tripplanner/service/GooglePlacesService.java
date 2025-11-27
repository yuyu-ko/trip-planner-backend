package com.example.tripplanner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class GooglePlacesService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Double getPlaceRating(String placeName) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/findplacefromtext/json")
                    .queryParam("input", placeName)
                    .queryParam("inputtype", "textquery")
                    .queryParam("fields", "rating")
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            ResponseEntity<GooglePlacesResponse> response =
                    restTemplate.getForEntity(uri, GooglePlacesResponse.class);

            if (response.getBody() != null &&
                    response.getBody().candidates != null &&
                    !response.getBody().candidates.isEmpty()) {

                return response.getBody().candidates.get(0).rating;
            }

            return null;
        } catch (Exception e) {
            System.out.println("Google API error: " + e.getMessage());
            return null;
        }
    }

    // ============================
//  New: Google Places Photo
// ============================
    public String getPlaceId(String placeName) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/findplacefromtext/json")
                    .queryParam("input", placeName)
                    .queryParam("inputtype", "textquery")
                    .queryParam("fields", "place_id")
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            String json = restTemplate.getForObject(uri, String.class);
            if (json == null) return null;

            var node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            var candidates = node.get("candidates");
            if (candidates == null || candidates.isEmpty()) return null;

            return candidates.get(0).get("place_id").asText();

        } catch (Exception e) {
            System.out.println("Google API place_id error: " + e.getMessage());
            return null;
        }
    }

    public String getPhotoReference(String placeId) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/details/json")
                    .queryParam("place_id", placeId)
                    .queryParam("fields", "photos")
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            String json = restTemplate.getForObject(uri, String.class);
            if (json == null) return null;

            var node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            var photosNode = node.path("result").path("photos");

            if (photosNode.isMissingNode() || !photosNode.isArray() || photosNode.isEmpty())
                return null;

            return photosNode.get(0).get("photo_reference").asText();

        } catch (Exception e) {
            System.out.println("Google API photo_ref error: " + e.getMessage());
            return null;
        }
    }

    public String generatePhotoUrl(String photoReference) {
        if (photoReference == null) return null;

        return "https://maps.googleapis.com/maps/api/place/photo"
                + "?maxwidth=800"
                + "&photo_reference=" + photoReference
                + "&key=" + apiKey;
    }

    /** 直接給地點文字 → 取得可用的 Google 圖片 URL */
    public String getPlacePhotoUrl(String placeName) {
        String placeId = getPlaceId(placeName);
        if (placeId == null) return null;

        String photoRef = getPhotoReference(placeId);
        //System.out.println("PHOTO URL → " + generatePhotoUrl(photoRef));
        return generatePhotoUrl(photoRef);
    }


    // DTO for Google API
    public static class GooglePlacesResponse {
        public java.util.List<Candidate> candidates;
    }

    public static class Candidate {
        public Double rating;
    }


}

