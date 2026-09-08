package com.crimsonlogic.open_petal_backend.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing a error d t o.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {
    private boolean success;
    private String message;
    private Integer status;
    private String stackTrace;
}
