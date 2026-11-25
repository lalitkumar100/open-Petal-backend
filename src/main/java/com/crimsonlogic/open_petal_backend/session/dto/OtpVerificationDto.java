package com.crimsonlogic.open_petal_backend.session.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationDto {
    @NotBlank(message = "OTP is required")
    private String otp;
}
