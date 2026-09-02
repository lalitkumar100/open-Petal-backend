package com.crimsonlogic.open_petal_backend.dto.skill;

import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserSkillDto {

    private SkillLevel skillLevel;

    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer experienceYears;

    private TeachingMode teachingMode;

    @Min(value = 5, message = "Minimum credits per session is 5")
    @Max(value = 20, message = "Maximum credits per session is 20")
    private Integer creditsPerSession;

    private String description;
}