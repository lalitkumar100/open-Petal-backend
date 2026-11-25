package com.crimsonlogic.open_petal_backend.session.controller;

import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.session.dto.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionConflictRequestDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionOtpRequestDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.session.dto.SubmitStoryRequestDto;
import com.crimsonlogic.open_petal_backend.session.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.session.service.SessionService;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final MessageService messageService;

    private final SessionService sessionService;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Long>> createSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateSessionDto dto) {
        
        Long learnerId = authenticateAndGetUserId(authHeader);
        LearningSession session = sessionService.createSession(learnerId, dto);
        
        ApiResponse<Long> response = ApiResponse.success(MessageCodes.SESSION_CREATED, messageService.getMessage(MessageCodes.SESSION_CREATED), session.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-sessions")
    public ResponseEntity<ApiResponse<List<SessionSummaryResponseDto>>> getMySessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = authenticateAndGetUserId(authHeader);
        List<SessionSummaryResponseDto> sessions = sessionService.getMySessions(userId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSIONS_RETRIEVED, messageService.getMessage(MessageCodes.SESSIONS_RETRIEVED), sessions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionDetailResponseDto>> getSessionDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        SessionDetailResponseDto detail = sessionService.getSessionDetails(id, userId);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.SESSION_DETAILS_RETRIEVED,
                messageService.getMessage(MessageCodes.SESSION_DETAILS_RETRIEVED),
                detail));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        sessionService.cancelSession(id, userId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_CANCELLED, messageService.getMessage(MessageCodes.SESSION_CANCELLED), null));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        sessionService.acceptSession(id, userId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_ACCEPTED, messageService.getMessage(MessageCodes.SESSION_ACCEPTED), null));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<ApiResponse<Void>> declineSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.declineSession(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_DECLINED, messageService.getMessage(MessageCodes.SESSION_DECLINED), null));
    }

    @GetMapping("/{id}/my-otp")
    public ResponseEntity<ApiResponse<String>> getMyOtp(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        String otp = sessionService.getMyOtp(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_OTP_RETRIEVED, messageService.getMessage(MessageCodes.SESSION_OTP_RETRIEVED), otp));
    }

    @PostMapping("/{id}/verify-start")
    public ResponseEntity<ApiResponse<Void>> verifyStart(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody SessionOtpRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.verifyStart(id, currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_START_VERIFIED, messageService.getMessage(MessageCodes.SESSION_START_VERIFIED), null));
    }
    @GetMapping("/{id}/conflict")
    public ResponseEntity<ApiResponse<com.crimsonlogic.open_petal_backend.session.dto.SessionConflictDetailResponseDto>> getSessionConflict(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        com.crimsonlogic.open_petal_backend.session.dto.SessionConflictDetailResponseDto conflict = sessionService.getSessionConflict(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_CONFLICT_DETAILS_RETRIEVED, messageService.getMessage(MessageCodes.SESSION_CONFLICT_DETAILS_RETRIEVED), conflict));
    }

    @PutMapping("/{id}/conflict/story")
    public ResponseEntity<ApiResponse<Void>> submitConflictStory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody com.crimsonlogic.open_petal_backend.session.dto.SubmitStoryRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.submitConflictStory(id, currentUserId, request.getStory());
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_CONFLICT_STORY_SUBMITTED, messageService.getMessage(MessageCodes.SESSION_CONFLICT_STORY_SUBMITTED), null));
    }

    @PostMapping("/{id}/verify-end")
    public ResponseEntity<ApiResponse<Void>> verifyEnd(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody SessionOtpRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.verifyEnd(id, currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_END_VERIFIED, messageService.getMessage(MessageCodes.SESSION_END_VERIFIED), null));
    }

    @PostMapping("/{id}/raise-conflict")
    public ResponseEntity<ApiResponse<Void>> raiseConflict(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody SessionConflictRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.raiseConflict(id, currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SESSION_CONFLICT_RAISED, messageService.getMessage(MessageCodes.SESSION_CONFLICT_RAISED), null));
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
