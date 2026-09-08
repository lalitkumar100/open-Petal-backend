package com.crimsonlogic.open_petal_backend.dto.skill;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a skill detail response dto.
 */
@Data
@Builder
public class SkillDetailResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean isActive;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private long learnersCount;
    private long teachersCount;
}
