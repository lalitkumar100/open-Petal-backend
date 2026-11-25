package com.crimsonlogic.open_petal_backend.skill.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

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
