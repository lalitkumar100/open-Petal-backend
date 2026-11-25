package com.crimsonlogic.open_petal_backend.admin.dto;

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
