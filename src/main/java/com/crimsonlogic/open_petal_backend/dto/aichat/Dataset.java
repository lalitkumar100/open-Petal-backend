package com.crimsonlogic.open_petal_backend.dto.aichat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object representing a dataset.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dataset {
    private int rows;
    private int columns;
    private String label;
    private List<Map<String, Object>> data;
}
