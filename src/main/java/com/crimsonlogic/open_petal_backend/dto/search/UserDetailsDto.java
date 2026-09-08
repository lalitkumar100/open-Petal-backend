package com.crimsonlogic.open_petal_backend.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing a user details dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDto {
    private UserInfoDto userInfo;
    private List<SkillBadgeDto> skillsOffered;
    private List<SkillBadgeDto> learningGoals;
    private String connectionStatus;
    private boolean isConnectionSender;
    private Long connectionRequestId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfoDto {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String status;
        private String role;
        private Integer credit;
        private String createdAt;
    }
}
