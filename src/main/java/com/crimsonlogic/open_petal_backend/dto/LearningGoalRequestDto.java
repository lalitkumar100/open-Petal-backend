package com.crimsonlogic.open_petal_backend.dto;

import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class LearningGoalRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Current level is required")
    private SkillLevel currentLevel;

    @NotNull(message = "Target level is required")
    private SkillLevel targetLevel;
}
