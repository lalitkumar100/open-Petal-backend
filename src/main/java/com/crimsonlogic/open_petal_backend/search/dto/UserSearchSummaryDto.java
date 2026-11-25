package com.crimsonlogic.open_petal_backend.search.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
