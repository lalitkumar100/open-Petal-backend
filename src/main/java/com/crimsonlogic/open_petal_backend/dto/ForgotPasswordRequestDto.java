package com.crimsonlogic.open_petal_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDto {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    private String email;
}
