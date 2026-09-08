package com.crimsonlogic.open_petal_backend.dto.session;

import lombok.Data;

/**
 * Data Transfer Object representing a session otp request dto.
 */
@Data
public class SessionOtpRequestDto {
    private String otp;
}
