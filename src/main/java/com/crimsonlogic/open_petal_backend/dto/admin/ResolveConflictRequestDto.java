package com.crimsonlogic.open_petal_backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object representing a resolve conflict request dto.
 */
@Data
public class ResolveConflictRequestDto {
    @NotBlank
    private String resolutionAction; // REFUND_LEARNER, PAYOUT_MENTOR, DISMISS
    
    private String adminNotes;
}
