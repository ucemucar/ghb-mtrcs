package com.ucemucar.utility.copilot_metrics.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class LanguageSummary {
    @JsonProperty("name")
    private String name;

    @JsonProperty("total_engaged_users")
    private int totalEngagedUsers;
}