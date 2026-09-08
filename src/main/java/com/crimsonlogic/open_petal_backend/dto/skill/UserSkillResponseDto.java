package com.crimsonlogic.open_petal_backend.dto.skill;

import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing a user skill response dto.
 */
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
