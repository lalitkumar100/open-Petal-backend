package com.crimsonlogic.open_petal_backend.dto.admin;

import lombok.Data;

/**
 * Data Transfer Object representing a admin cancel session request dto.
 */
@Data
public class AdminCancelSessionRequestDto {
    private String reason;
}
