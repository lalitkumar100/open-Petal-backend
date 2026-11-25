package com.crimsonlogic.open_petal_backend.auth.dto;

import com.crimsonlogic.open_petal_backend.auth.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.auth.enums.RoleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

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
