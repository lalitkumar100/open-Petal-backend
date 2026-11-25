package com.crimsonlogic.open_petal_backend.session.dto;

import com.crimsonlogic.open_petal_backend.session.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.session.enums.SessionType;
import com.crimsonlogic.open_petal_backend.skill.enums.TeachingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
