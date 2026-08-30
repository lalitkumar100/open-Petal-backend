package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.LoginRequestDto;
import com.crimsonlogic.open_petal_backend.dto.LoginResponseDto;
import com.crimsonlogic.open_petal_backend.dto.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.dto.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.dto.ChangePasswordRequestDto;
import com.crimsonlogic.open_petal_backend.dto.ForgotPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.dto.ResetPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;

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
