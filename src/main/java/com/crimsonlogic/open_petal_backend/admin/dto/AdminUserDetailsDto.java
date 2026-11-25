package com.crimsonlogic.open_petal_backend.admin.dto;

import com.crimsonlogic.open_petal_backend.user.dto.UserProfileDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
