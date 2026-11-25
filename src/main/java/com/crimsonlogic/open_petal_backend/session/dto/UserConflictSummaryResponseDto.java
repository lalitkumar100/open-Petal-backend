package com.crimsonlogic.open_petal_backend.session.dto;

import com.crimsonlogic.open_petal_backend.session.enums.ConflictReason;
import com.crimsonlogic.open_petal_backend.session.enums.ConflictStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
