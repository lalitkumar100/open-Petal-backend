package com.crimsonlogic.open_petal_backend.dto.query;

import com.crimsonlogic.open_petal_backend.enums.QueryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object representing a admin query reply dto.
 */
@Data
public class AdminQueryReplyDto {
    @NotNull(message = "Status is required")
    private QueryStatus status;

    private String adminResponse;
}
