package com.crimsonlogic.open_petal_backend.auth.service;

import com.crimsonlogic.open_petal_backend.auth.dto.ForgotPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.auth.dto.LoginRequestDto;
import com.crimsonlogic.open_petal_backend.auth.dto.LoginResponseDto;
import com.crimsonlogic.open_petal_backend.auth.dto.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.auth.dto.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.auth.dto.ResetPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.auth.entity.Login;
import com.crimsonlogic.open_petal_backend.auth.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.user.dto.ChangePasswordRequestDto;

public interface AuthService {
    RegisterResponseDto register(RegisterRequestDto request);
    LoginResponseDto login(LoginRequestDto request);
    void verifyEmail(String token);
    void changePassword(String email, ChangePasswordRequestDto request);
    void forgotPassword(ForgotPasswordRequestDto request);
    void resetPassword(String token, ResetPasswordRequestDto request);
    Login changeSelfStatus(String email, AccountStatus newStatus);
    boolean validateToken(String token);
    String getRoleFromToken(String token);
    String getEmailFromToken(String token);
}
