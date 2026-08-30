package com.crimsonlogic.open_petal_backend.dto;

import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import com.crimsonlogic.open_petal_backend.enumerator.Gender;
import com.crimsonlogic.open_petal_backend.enumerator.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileDto {
    private Long userId;
    private Long authId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private Gender gender;
    private AccountStatus status;
    private RoleType role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
