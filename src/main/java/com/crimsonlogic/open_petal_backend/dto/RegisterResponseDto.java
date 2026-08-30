package com.crimsonlogic.open_petal_backend.dto;

import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import com.crimsonlogic.open_petal_backend.enumerator.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegisterResponseDto {
    private Long userId;
    private Long authId;
    private String email;
    private String firstName;
    private String lastName;
    private RoleType role;
    private AccountStatus status;
    private LocalDateTime createdAt;
}
