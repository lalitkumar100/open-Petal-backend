package com.crimsonlogic.open_petal_backend.admin.dto;

import com.crimsonlogic.open_petal_backend.session.enums.ConflictReason;
import com.crimsonlogic.open_petal_backend.session.enums.ConflictStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminConflictDetailResponseDto {
    private Long conflictId;
    private Long sessionId;
    private String learnerName;
    private String mentorName;
    private String primarySkillName;
    private String sessionMode;
    private String sessionType;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer creditsHeld;
    private String learnerStory;
    private String mentorStory;
    private ConflictReason reason;
    private ConflictStatus status;
    private String raisedByName;
    private String adminResolutionNotes;
    private String resolvedByName;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}
