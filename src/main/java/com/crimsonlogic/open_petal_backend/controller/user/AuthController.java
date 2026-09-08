package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.auth.LoginRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.LoginResponseDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.dto.auth.ForgotPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.ResetPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication and authorization.
 * Provides endpoints for registration, login, and password management.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     *
     * @param request the registration details
     * @return ResponseEntity containing RegisterResponseDto wrapped in an ApiResponse
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        RegisterResponseDto responseData = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", responseData));
    }

    /**
     * Authenticates a user and issues a JWT token.
     *
     * @param request the login credentials
     * @return ResponseEntity containing LoginResponseDto wrapped in an ApiResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto responseData = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", responseData));
    }

    /**
     * Verifies a user's email address using a token.
     *
     * @param token the verification token sent via email
     * @return ResponseEntity indicating success
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully", null));
    }

    /**
     * Initiates the password reset process by sending an email.
     *
     * @param request the payload containing the user's email address
     * @return ResponseEntity indicating the email was sent
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset email sent", null));
    }

    /**
     * Resets a user's password using a valid reset token.
     *
     * @param token the password reset token
     * @param request the payload containing the new password
     * @return ResponseEntity indicating success
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam("token") String token, 
                                                             @Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(token, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successfully", null));
    }
}
