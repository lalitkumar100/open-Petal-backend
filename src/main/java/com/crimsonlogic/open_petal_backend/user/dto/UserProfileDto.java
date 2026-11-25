package com.crimsonlogic.open_petal_backend.user.dto;

import com.crimsonlogic.open_petal_backend.auth.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.auth.enums.RoleType;
import com.crimsonlogic.open_petal_backend.user.enums.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    private Long userId;
    private Long authId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dob;
    private Gender gender;
    private String description;
    private Integer credit;
    private AccountStatus status;
    private RoleType role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
