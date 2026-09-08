package com.crimsonlogic.open_petal_backend.dto.conflict;

import com.crimsonlogic.open_petal_backend.enums.ConflictReason;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a user conflict summary response dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConflictSummaryResponseDto {
    private Long conflictId;
    private Long sessionId;
    private String partnerName;
    private String myRole; // "LEARNER" or "MENTOR"
    private String primarySkillName;
    private ConflictReason reason;
    private ConflictStatus status;
    private boolean raisedByMe;
    private LocalDateTime createdAt;
}
