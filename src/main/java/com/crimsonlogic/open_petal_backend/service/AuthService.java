package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.auth.LoginRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.LoginResponseDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.dto.user.ChangePasswordRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.ForgotPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.ResetPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.enums.AccountStatus;

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
