package com.crimsonlogic.open_petal_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {
    private boolean success;
    private String message;
    private Integer status;
    private String stackTrace;
}
