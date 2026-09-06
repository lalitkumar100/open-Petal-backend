package com.crimsonlogic.open_petal_backend.dto.admin;

import com.crimsonlogic.open_petal_backend.dto.user.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailsDto {

    private UserProfileDto userInfo;
    private List<SkillSummaryDto> skillsOffered;
    private List<SkillSummaryDto> learningGoals;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillSummaryDto {
        private String name;
        private String level; // For offered: skillLevel. For learning: targetLevel (or currentLevel depending on preference)
    }
}
