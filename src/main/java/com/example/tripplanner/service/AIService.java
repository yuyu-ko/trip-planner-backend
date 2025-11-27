package com.example.tripplanner.service;

import com.example.tripplanner.dto.CreateTripRequest;
import com.example.tripplanner.dto.ItineraryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class AIService {

    private final WebClient webClient;

    public AIService(WebClient.Builder builder, @Value("${ai.service.url}") String aiServiceUrl) {
        this.webClient = builder
                .baseUrl(aiServiceUrl)
                .build();
    }

    public Mono<ItineraryResponse> generateItinerary(CreateTripRequest req) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = mapper.writeValueAsString(req);
        System.out.println("=== RAW JSON SENT ===");
        System.out.println(json);

        return webClient.post()
                .uri("/generate")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ItineraryResponse.class);
    }
}
