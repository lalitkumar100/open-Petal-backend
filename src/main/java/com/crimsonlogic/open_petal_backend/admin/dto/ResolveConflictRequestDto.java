package com.crimsonlogic.open_petal_backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveConflictRequestDto {
    @NotBlank
    private String resolutionAction; // REFUND_LEARNER, PAYOUT_MENTOR, DISMISS
    
    private String adminNotes;
}
