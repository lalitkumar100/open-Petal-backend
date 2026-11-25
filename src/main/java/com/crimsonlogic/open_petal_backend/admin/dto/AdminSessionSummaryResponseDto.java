package com.crimsonlogic.open_petal_backend.admin.dto;

import com.crimsonlogic.open_petal_backend.session.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.session.enums.SessionType;
import com.crimsonlogic.open_petal_backend.skill.enums.TeachingMode;
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
