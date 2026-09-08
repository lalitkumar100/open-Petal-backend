package com.crimsonlogic.open_petal_backend.dto.session;

import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a session conflict detail response dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionConflictDetailResponseDto {
    private Long conflictId;
    private Long sessionId;
    private ConflictStatus status;
    private String raisedByName;
    private boolean raisedByMe;
    private String learnerStory;
    private String mentorStory;
    private String adminResolutionNotes;
    private LocalDateTime createdAt;
}
