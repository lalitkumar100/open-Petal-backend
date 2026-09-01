package com.crimsonlogic.open_petal_backend.dto.query;

import com.crimsonlogic.open_petal_backend.enumerator.QueryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminQueryReplyDto {
    @NotNull(message = "Status is required")
    private QueryStatus status;

    private String adminResponse;
}
