package com.crimsonlogic.open_petal_backend.dto.skill;

import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningGoalResponseDto {
    private Long id;
    private Long skillId;
    private String skillName;
    private SkillLevel currentLevel;
    private SkillLevel targetLevel;
    private Boolean isActive;
}
