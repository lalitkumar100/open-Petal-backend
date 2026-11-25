package com.crimsonlogic.open_petal_backend.skill.dto;

import com.crimsonlogic.open_petal_backend.skill.entity.Skill;
import com.crimsonlogic.open_petal_backend.skill.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
