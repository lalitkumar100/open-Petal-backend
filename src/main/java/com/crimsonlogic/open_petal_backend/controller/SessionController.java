package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.SessionService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crimsonlogic.open_petal_backend.dto.session.SessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionOtpRequestDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionConflictRequestDto;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Long>> createSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateSessionDto dto) {
        
        Long learnerId = authenticateAndGetUserId(authHeader);
        LearningSession session = sessionService.createSession(learnerId, dto);
        
        ApiResponse<Long> response = new ApiResponse<>(true, "Session created successfully", session.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-sessions")
    public ResponseEntity<ApiResponse<List<SessionSummaryResponseDto>>> getMySessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = authenticateAndGetUserId(authHeader);
        List<SessionSummaryResponseDto> sessions = sessionService.getMySessions(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sessions retrieved", sessions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionDetailResponseDto>> getSessionDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        SessionDetailResponseDto detail = sessionService.getSessionDetails(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session details retrieved", detail));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        sessionService.cancelSession(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session cancelled successfully", null));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<String>> acceptSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        sessionService.acceptSession(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session accepted successfully", null));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<ApiResponse<String>> declineSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.declineSession(id, currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session request declined successfully", null));
    }

    @GetMapping("/{id}/my-otp")
    public ResponseEntity<ApiResponse<String>> getMyOtp(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        String otp = sessionService.getMyOtp(id, currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP retrieved successfully", otp));
    }

    @PostMapping("/{id}/verify-start")
    public ResponseEntity<ApiResponse<String>> verifyStart(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody SessionOtpRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.verifyStart(id, currentUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Start OTP verified successfully", null));
    }
    @GetMapping("/{id}/conflict")
    public ResponseEntity<ApiResponse<com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto>> getSessionConflict(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto conflict = sessionService.getSessionConflict(id, currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Conflict details retrieved successfully", conflict));
    }

    @PutMapping("/{id}/conflict/story")
    public ResponseEntity<ApiResponse<String>> submitConflictStory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody com.crimsonlogic.open_petal_backend.dto.session.SubmitStoryRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.submitConflictStory(id, currentUserId, request.getStory());
        return ResponseEntity.ok(new ApiResponse<>(true, "Counter-story submitted successfully", null));
    }


    @PostMapping("/{id}/verify-end")
    public ResponseEntity<ApiResponse<String>> verifyEnd(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody SessionOtpRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.verifyEnd(id, currentUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "End OTP verified successfully", null));
    }

    @PostMapping("/{id}/raise-conflict")
    public ResponseEntity<ApiResponse<String>> raiseConflict(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody SessionConflictRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.raiseConflict(id, currentUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Conflict raised successfully", null));
    }

    private Long authenticateAndGetUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }
        String email = authService.getEmailFromToken(token);
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"))
                .getId();
    }
}
