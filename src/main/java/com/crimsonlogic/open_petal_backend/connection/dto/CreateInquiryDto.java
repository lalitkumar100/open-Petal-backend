package com.crimsonlogic.open_petal_backend.connection.dto;

import com.crimsonlogic.open_petal_backend.skill.entity.Skill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInquiryDto {

    @NotNull(message = "Mentor ID is required")
    private Long receiverId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotBlank(message = "Please include a brief introductory message")
    private String message;
}
