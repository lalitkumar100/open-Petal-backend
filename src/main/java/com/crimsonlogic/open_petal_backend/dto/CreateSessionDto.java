package com.crimsonlogic.open_petal_backend.dto;

import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateSessionDto {

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Primary Skill ID is required")
    private Long primarySkillId;

    @NotNull(message = "Session Type is required")
    private SessionType sessionType;

    private Long offeredSkillId;

    @NotNull(message = "Teaching Mode is required")
    private TeachingMode mode;

    private Long locationId;

    @org.hibernate.validator.constraints.URL(message = "Invalid meeting link format")
    private String meetingLink;

    @NotNull
    @FutureOrPresent(message = "Session date must be today or in the future")
    private LocalDate sessionDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull(message = "Credits held is required")
    @Min(value = 0, message = "Credits cannot be negative")
    private Integer creditsHeld;
}

