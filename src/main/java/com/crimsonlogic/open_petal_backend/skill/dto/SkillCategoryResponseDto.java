package com.crimsonlogic.open_petal_backend.skill.dto;

import com.crimsonlogic.open_petal_backend.skill.entity.SkillCategory;
import java.time.LocalDateTime;

public record SkillCategoryResponseDto(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isDeleted
) {

    public static SkillCategoryResponseDto from(SkillCategory category) {
        return new SkillCategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getIsDeleted());
    }
}
