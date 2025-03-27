package com.ucemucar.utility.copilot_metrics.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CombinedResponse {
    private List<Seat> seats;
    private List<DailyMetricsResponse> metrics;

}