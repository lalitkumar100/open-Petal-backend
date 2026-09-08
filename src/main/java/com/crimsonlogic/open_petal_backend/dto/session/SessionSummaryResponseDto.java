package com.crimsonlogic.open_petal_backend.dto.session;

import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object representing a session summary response dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionSummaryResponseDto {
    private Long id;
    private String partnerName;
    private String role; // "LEARNER" or "MENTOR"
    private String primarySkillName;
    private SessionType sessionType;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private TeachingMode mode;
    private SessionStatus status;
}
