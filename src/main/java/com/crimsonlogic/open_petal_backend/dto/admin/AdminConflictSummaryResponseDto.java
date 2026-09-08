package com.crimsonlogic.open_petal_backend.dto.admin;

import com.crimsonlogic.open_petal_backend.enums.ConflictReason;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a admin conflict summary response dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminConflictSummaryResponseDto {
    private Long conflictId;
    private Long sessionId;
    private String learnerName;
    private String mentorName;
    private String primarySkillName;
    private ConflictReason reason;
    private ConflictStatus status;
    private Integer creditsHeld;
    private String raisedByName;
    private LocalDateTime createdAt;
}
