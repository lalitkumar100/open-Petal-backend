package com.crimsonlogic.open_petal_backend.dto.session;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object representing a otp verification dto.
 */
@Data
public class OtpVerificationDto {
    @NotBlank(message = "OTP is required")
    private String otp;
}
