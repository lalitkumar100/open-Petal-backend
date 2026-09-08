package com.crimsonlogic.open_petal_backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object representing a forgot password request dto.
 */
@Data
public class ForgotPasswordRequestDto {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    private String email;
}
