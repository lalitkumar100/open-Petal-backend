package com.crimsonlogic.open_petal_backend.session.dto;

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
