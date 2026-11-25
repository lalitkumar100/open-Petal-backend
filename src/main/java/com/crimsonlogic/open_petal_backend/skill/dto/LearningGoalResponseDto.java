package com.crimsonlogic.open_petal_backend.skill.dto;

import com.crimsonlogic.open_petal_backend.skill.enums.SkillLevel;
import java.util.Map;
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
    private Map<String, Object> roadplan;
}
