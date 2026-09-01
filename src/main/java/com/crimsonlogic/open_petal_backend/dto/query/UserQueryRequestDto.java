package com.crimsonlogic.open_petal_backend.dto.query;

import com.crimsonlogic.open_petal_backend.enumerator.QueryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserQueryRequestDto {
    @NotNull(message = "Query type is required")
    private QueryType queryType;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;
}
