package com.crimsonlogic.open_petal_backend.dto.session;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object representing a submit story request dto.
 */
@Data
public class SubmitStoryRequestDto {
    @NotBlank(message = "Story statement cannot be empty")
    private String story;
}
