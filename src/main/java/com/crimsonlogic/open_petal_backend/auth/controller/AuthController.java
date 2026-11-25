package com.crimsonlogic.open_petal_backend.auth.controller;

import com.crimsonlogic.open_petal_backend.auth.dto.ForgotPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.auth.dto.LoginRequestDto;
import com.crimsonlogic.open_petal_backend.auth.dto.LoginResponseDto;
import com.crimsonlogic.open_petal_backend.auth.dto.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.auth.dto.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.auth.dto.ResetPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.auth.entity.Login;
import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final MessageService messageService;

    private final AuthService authService;

    public AuthController(AuthService authService,
                           MessageService messageService) {
        this.messageService = messageService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        RegisterResponseDto responseData = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageCodes.AUTH_REGISTER_SUCCESS, messageService.getMessage(MessageCodes.AUTH_REGISTER_SUCCESS), responseData));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto responseData = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.AUTH_LOGIN_SUCCESS, messageService.getMessage(MessageCodes.AUTH_LOGIN_SUCCESS), responseData));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.AUTH_EMAIL_VERIFIED, messageService.getMessage(MessageCodes.AUTH_EMAIL_VERIFIED), null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.AUTH_PASSWORD_RESET_EMAIL_SENT, messageService.getMessage(MessageCodes.AUTH_PASSWORD_RESET_EMAIL_SENT), null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam("token") String token, 
                                                             @Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(token, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.AUTH_PASSWORD_RESET, messageService.getMessage(MessageCodes.AUTH_PASSWORD_RESET), null));
    }
}
