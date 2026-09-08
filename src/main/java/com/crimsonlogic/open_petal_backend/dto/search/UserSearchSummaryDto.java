package com.crimsonlogic.open_petal_backend.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing a user search summary dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchSummaryDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String description;
    private List<SkillBadgeDto> learningSkills;
    private List<SkillBadgeDto> techSkills;
}
