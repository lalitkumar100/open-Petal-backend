package com.crimsonlogic.open_petal_backend.dto.admin;

import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Data Transfer Object representing a admin session summary response dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSessionSummaryResponseDto {
    private Long id;
    private String learnerName;
    private String learnerEmail;
    private String mentorName;
    private String mentorEmail;
    private String primarySkillName;
    private SessionType sessionType;
    private Integer creditsHeld;
    private TeachingMode mode;
    private SessionStatus status;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
}
