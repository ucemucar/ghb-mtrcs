package com.ucemucar.utility.copilot_metrics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucemucar.utility.copilot_metrics.model.CombinedResponse;
import com.ucemucar.utility.copilot_metrics.model.CopilotSeatsResponse;
import com.ucemucar.utility.copilot_metrics.model.DailyMetricsResponse;
import com.ucemucar.utility.copilot_metrics.model.Seat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class GitHubMetricsService {

    @Value("${github.api.seats.url}")
    private String githubApiSeatsUrl;

    @Value("${github.api.metrics.url}")
    private String githubApiMetricsUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GitHubMetricsService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public Mono<CombinedResponse> fetchCombinedData() {
        // Seat verilerini çeken Mono
        Mono<CopilotSeatsResponse> seatsMono = webClient.get()
                .uri(githubApiSeatsUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseJson(json, new TypeReference<CopilotSeatsResponse>() {}, "GitHub Copilot Seats"));

        // Metrics verilerini çeken Mono
        Mono<List<DailyMetricsResponse>> metricsMono = webClient.get()
                .uri(githubApiMetricsUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseJson(json, new TypeReference<List<DailyMetricsResponse>>() {}, "GitHub Metrics"));

        // İki Mono'yu birleştirip CombinedResponse oluştur
        return Mono.zip(seatsMono, metricsMono)
                .map(tuple -> new CombinedResponse(tuple.getT1(), tuple.getT2()));
    }

    // JSON parse eden yardımcı metod (varsayılan olarak mevcut)
    private <T> T parseJson(String json, TypeReference<T> typeReference, String context) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON for " + context + ": " + e.getMessage(), e);
        }
    }

    public Mono<List<DailyMetricsResponse>> fetchMetrics() {
        Mono<List<DailyMetricsResponse>> metricsMono = webClient.get()
                .uri(githubApiMetricsUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseJson(json, new TypeReference<List<DailyMetricsResponse>>() {}, "GitHub Metrics"));

        return metricsMono;

    }

    public Mono<List<Seat>> fetchSeats() {
        return webClient.get()
                .uri(githubApiSeatsUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseJson(json, new TypeReference<List<Seat>>() {}, "GitHub Copilot Seats"));
    }

}