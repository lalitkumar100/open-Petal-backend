package com.crimsonlogic.open_petal_backend.dto.skill;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SkillVerificationRequestDto {
    @NotEmpty(message = "Answers cannot be empty")
    @Size(min = 5, max = 5, message = "Exactly 5 answers must be provided")
    private List<String> answers;
}
