package com.crimsonlogic.open_petal_backend.dto.aichat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object representing a chat response.
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponse {
    private String chatId;
    private String title;
    private AiText aiText;
    private List<Dataset> fromDatabase;
    private boolean canvas;
    private String nextGenSummary;
    private String intentExplanation;
    
    // Internal fields used only during parsing from LLM, ignored by frontend:
    private SqlQuery sqlQuery;
    private List<SqlQuery> queryList;
    private String emptyResultFallback;
}
