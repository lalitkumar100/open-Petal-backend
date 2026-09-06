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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSessionDetailResponseDto extends AdminSessionSummaryResponseDto {
    private String locationName;
    private String meetingLink;
    private String learnerStartPassword;
    private String mentorStartPassword;
    private String learnerEndPassword;
    private String mentorEndPassword;
    private String offeredSkillName;

    @Builder(builderMethodName = "adminDetailBuilder")
    public AdminSessionDetailResponseDto(Long id, String learnerName, String learnerEmail, String mentorName, String mentorEmail, String primarySkillName, SessionType sessionType, Integer creditsHeld, TeachingMode mode, SessionStatus status, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, LocalDateTime createdAt, String locationName, String meetingLink, String learnerStartPassword, String mentorStartPassword, String learnerEndPassword, String mentorEndPassword, String offeredSkillName) {
        super(id, learnerName, learnerEmail, mentorName, mentorEmail, primarySkillName, sessionType, creditsHeld, mode, status, sessionDate, startTime, endTime, createdAt);
        this.locationName = locationName;
        this.meetingLink = meetingLink;
        this.learnerStartPassword = learnerStartPassword;
        this.mentorStartPassword = mentorStartPassword;
        this.learnerEndPassword = learnerEndPassword;
        this.mentorEndPassword = mentorEndPassword;
        this.offeredSkillName = offeredSkillName;
    }
}
