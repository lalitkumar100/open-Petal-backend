package com.crimsonlogic.open_petal_backend.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillBadgeDto {
    private Long id;
    private String name;
    private String level;
    private Integer creditsPerSession;
    private Integer sessionDurationMin;
}
