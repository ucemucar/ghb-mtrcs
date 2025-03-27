package com.ucemucar.utility.copilot_metrics.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
public class CodeCompletionsMetrics {
    @JsonProperty("total_engaged_users")
    private int totalEngagedUsers;

    @JsonProperty("languages")
    private List<LanguageSummary> languages;

    @JsonProperty("editors")
    private List<EditorMetrics> editors;
}