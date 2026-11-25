package com.crimsonlogic.open_petal_backend.auth.dto;

import com.crimsonlogic.open_petal_backend.auth.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.auth.enums.RoleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String token;
    private String tokenType;
    private long expiresIn;
    private Long userId;
    private String email;
    private String fullName;
    private RoleType role;
    private AccountStatus status;
}
