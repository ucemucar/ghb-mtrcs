package com.ucemucar.utility.copilot_metrics.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummaryStats {
    private int totalAssigned;
    private int assignedButNeverUsed;
    private int noActivityLast7Days;
}
