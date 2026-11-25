package com.crimsonlogic.open_petal_backend.skill.dto;

import com.crimsonlogic.open_petal_backend.skill.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.skill.enums.TeachingMode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSkillResponseDto {
    private Long id;
    private Long skillId;
    private String skillName;
    private SkillLevel level;
    private Integer experienceYears;
    private TeachingMode teachingMode;
    private Integer sessionDuration;
    private Integer creditsPerSession;
    private Double rating;
    private Integer peopleTaught;
    private Boolean isActive;
}
