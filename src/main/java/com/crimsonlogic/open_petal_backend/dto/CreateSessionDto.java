package com.crimsonlogic.open_petal_backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateSessionDto {
    @NotNull(message = "Learner ID is required")
    private Long learnerId;

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull
    @FutureOrPresent(message = "Session date must be today or in the future")
    private LocalDate sessionDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Min(value = 5, message = "Minimum 5 credits required")
    private Integer creditsToHold;
}

