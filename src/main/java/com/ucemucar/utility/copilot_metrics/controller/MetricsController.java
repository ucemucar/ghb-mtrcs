package com.ucemucar.utility.copilot_metrics.controller;

import com.ucemucar.utility.copilot_metrics.model.CombinedResponse;
import com.ucemucar.utility.copilot_metrics.model.DailyMetricsResponse;
import com.ucemucar.utility.copilot_metrics.model.Seat;
import com.ucemucar.utility.copilot_metrics.model.SummaryStats;
import com.ucemucar.utility.copilot_metrics.service.GitHubMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MetricsController {

    private final GitHubMetricsService metricsService;

    @GetMapping("/")
    public String showMetrics(Model model) {
        List<DailyMetricsResponse> dailyMetrics = metricsService.fetchMetrics().block();
        model.addAttribute("dailyMetrics", dailyMetrics);
        return "index";
    }

    @GetMapping("/2")
    public String getCombinedData(Model model) {
        try {
            CombinedResponse combinedResponse = metricsService.fetchCombinedData().block();
            model.addAttribute("combinedResponse", combinedResponse);
            return "index"; // Thymeleaf şablonunun adı (index.html)
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching data: " + e.getMessage());
            return "error"; // Hata durumunda error.html şablonuna yönlendir
        }
    }

    @GetMapping("/3")
    public String getUserActivity(Model model) {
        try {
            List<Seat> seats = metricsService.fetchSeats().block();

            // Özet istatistikleri hesapla
            int totalAssigned = seats.size();
            int assignedButNeverUsed = (int) seats.stream()
                    .filter(seat -> seat.getLastActivityAt() == null)
                    .count();
            long noActivityLast7Days = seats != null ? seats.stream()
                    .filter(seat -> seat.getLastActivityAt() != null &&
                            LocalDateTime.parse(seat.getLastActivityAt()).isBefore(LocalDateTime.now().minusDays(7)))
                    .count() : 0;

            SummaryStats summaryStats = new SummaryStats(totalAssigned, assignedButNeverUsed, noActivityLast7Days);

            model.addAttribute("seats", seats);
            model.addAttribute("summaryStats", summaryStats);
            return "user-activity";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching data: " + e.getMessage());
            return "error";
        }
    }
}