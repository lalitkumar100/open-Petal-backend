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

/**
 * Service interface for auth operations.
 */
public interface AuthService {
    /**
     * Register.
     */
    RegisterResponseDto register(RegisterRequestDto request);
    /**
     * Login.
     */
    LoginResponseDto login(LoginRequestDto request);
    /**
     * Verify Email.
     */
    void verifyEmail(String token);
    /**
     * Change Password.
     */
    void changePassword(String email, ChangePasswordRequestDto request);
    /**
     * Forgot Password.
     */
    void forgotPassword(ForgotPasswordRequestDto request);
    /**
     * Reset Password.
     */
    void resetPassword(String token, ResetPasswordRequestDto request);
    /**
     * Change Self Status.
     */
    Login changeSelfStatus(String email, AccountStatus newStatus);
    /**
     * Validate Token.
     */
    boolean validateToken(String token);
    /**
     * Get Role From Token.
     */
    String getRoleFromToken(String token);
    /**
     * Get Email From Token.
     */
    String getEmailFromToken(String token);
}
