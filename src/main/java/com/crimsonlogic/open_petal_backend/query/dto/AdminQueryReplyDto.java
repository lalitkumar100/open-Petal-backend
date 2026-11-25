package com.crimsonlogic.open_petal_backend.query.dto;

import com.crimsonlogic.open_petal_backend.query.enums.QueryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminQueryReplyDto {
    @NotNull(message = "Status is required")
    private QueryStatus status;

    private String adminResponse;
}
