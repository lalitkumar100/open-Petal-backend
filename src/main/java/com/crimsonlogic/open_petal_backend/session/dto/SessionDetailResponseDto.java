package com.crimsonlogic.open_petal_backend.session.dto;

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
@NoArgsConstructor
@AllArgsConstructor
public class SessionDetailResponseDto extends SessionSummaryResponseDto {
    private String offeredSkillName;
    private Integer creditsHeld;
    private String meetingLink;
    private String locationName;
    private LocalDateTime createdAt;
    
    private boolean isMentor;
    private boolean canAccept;
    private boolean canDecline;
    private boolean canCancel;
    
    private boolean learnerVerifiedStart;
    private boolean mentorVerifiedStart;
    private boolean learnerVerifiedEnd;
    private boolean mentorVerifiedEnd;

    @Builder(builderMethodName = "detailBuilder")
    public SessionDetailResponseDto(Long id, String partnerName, String role, String primarySkillName, SessionType sessionType, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, TeachingMode mode, SessionStatus status, String offeredSkillName, Integer creditsHeld, String meetingLink, String locationName, LocalDateTime createdAt, boolean isMentor, boolean canAccept, boolean canDecline, boolean canCancel, boolean learnerVerifiedStart, boolean mentorVerifiedStart, boolean learnerVerifiedEnd, boolean mentorVerifiedEnd) {
        super(id, partnerName, role, primarySkillName, sessionType, sessionDate, startTime, endTime, mode, status);
        this.offeredSkillName = offeredSkillName;
        this.creditsHeld = creditsHeld;
        this.meetingLink = meetingLink;
        this.locationName = locationName;
        this.createdAt = createdAt;
        this.isMentor = isMentor;
        this.canAccept = canAccept;
        this.canDecline = canDecline;
        this.canCancel = canCancel;
        this.learnerVerifiedStart = learnerVerifiedStart;
        this.mentorVerifiedStart = mentorVerifiedStart;
        this.learnerVerifiedEnd = learnerVerifiedEnd;
        this.mentorVerifiedEnd = mentorVerifiedEnd;
    }
}
