package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
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

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        RegisterResponseDto responseData = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", responseData));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto responseData = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", responseData));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset email sent", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam("token") String token, 
                                                             @Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(token, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successfully", null));
    }
}
