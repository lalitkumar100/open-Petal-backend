package com.crimsonlogic.open_petal_backend.dto.skill;

import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object representing a create learning goal dto.
 */
@Data
public class CreateLearningGoalDto {
    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Current level is required")
    private SkillLevel currentLevel;

    @NotNull(message = "Target level is required")
    private SkillLevel targetLevel;
}
