package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.session.CreateSessionDto;
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

/**
 * REST Controller for managing learning sessions.
 * Provides endpoints for scheduling, accepting, verifying, and resolving sessions.
 */
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final AuthService authService;
    private final UserService userService;

    /**
     * Creates a new learning session request.
     *
     * @param authHeader the authorization header containing the user token
     * @param dto the payload containing session scheduling details
     * @return ResponseEntity containing the new session's ID
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Long>> createSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateSessionDto dto) {
        
        Long learnerId = authenticateAndGetUserId(authHeader);
        LearningSession session = sessionService.createSession(learnerId, dto);
        
        ApiResponse<Long> response = new ApiResponse<>(true, "Session created successfully", session.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a summary of all sessions associated with the user.
     *
     * @param authHeader the authorization header containing the user token
     * @return ResponseEntity containing a list of SessionSummaryResponseDto
     */
    @GetMapping("/my-sessions")
    public ResponseEntity<ApiResponse<List<SessionSummaryResponseDto>>> getMySessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = authenticateAndGetUserId(authHeader);
        List<SessionSummaryResponseDto> sessions = sessionService.getMySessions(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sessions retrieved", sessions));
    }

    /**
     * Retrieves detailed information about a specific session.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session
     * @return ResponseEntity containing SessionDetailResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionDetailResponseDto>> getSessionDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        SessionDetailResponseDto detail = sessionService.getSessionDetails(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session details retrieved", detail));
    }

    /**
     * Cancels an upcoming or pending session.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session to cancel
     * @return ResponseEntity indicating success
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        sessionService.cancelSession(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session cancelled successfully", null));
    }

    /**
     * Accepts a pending session request (as a mentor).
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session to accept
     * @return ResponseEntity indicating success
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<String>> acceptSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = authenticateAndGetUserId(authHeader);
        sessionService.acceptSession(id, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session accepted successfully", null));
    }

    /**
     * Declines a pending session request.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session to decline
     * @return ResponseEntity indicating success
     */
    @PostMapping("/{id}/decline")
    public ResponseEntity<ApiResponse<String>> declineSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.declineSession(id, currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session request declined successfully", null));
    }

    /**
     * Retrieves the One-Time Password (OTP) for the current user's session role.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session
     * @return ResponseEntity containing the OTP string
     */
    @GetMapping("/{id}/my-otp")
    public ResponseEntity<ApiResponse<String>> getMyOtp(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        String otp = sessionService.getMyOtp(id, currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP retrieved successfully", otp));
    }

    /**
     * Verifies the start OTP provided by the other participant to begin the session.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session
     * @param request the payload containing the OTP
     * @return ResponseEntity indicating success
     */
    @PostMapping("/{id}/verify-start")
    public ResponseEntity<ApiResponse<String>> verifyStart(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody SessionOtpRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.verifyStart(id, currentUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Start OTP verified successfully", null));
    }
    /**
     * Retrieves conflict details for a disputed session.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session
     * @return ResponseEntity containing SessionConflictDetailResponseDto
     */
    @GetMapping("/{id}/conflict")
    public ResponseEntity<ApiResponse<com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto>> getSessionConflict(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto conflict = sessionService.getSessionConflict(id, currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Conflict details retrieved successfully", conflict));
    }

    /**
     * Submits a counter-story for an ongoing session conflict.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the disputed session
     * @param request the payload containing the counter-story
     * @return ResponseEntity indicating success
     */
    @PutMapping("/{id}/conflict/story")
    public ResponseEntity<ApiResponse<String>> submitConflictStory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody com.crimsonlogic.open_petal_backend.dto.session.SubmitStoryRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.submitConflictStory(id, currentUserId, request.getStory());
        return ResponseEntity.ok(new ApiResponse<>(true, "Counter-story submitted successfully", null));
    }


    /**
     * Verifies the end OTP provided by the other participant to conclude the session.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session
     * @param request the payload containing the OTP
     * @return ResponseEntity indicating success
     */
    @PostMapping("/{id}/verify-end")
    public ResponseEntity<ApiResponse<String>> verifyEnd(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody SessionOtpRequestDto request) {
        Long currentUserId = authenticateAndGetUserId(authHeader);
        sessionService.verifyEnd(id, currentUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "End OTP verified successfully", null));
    }

    /**
     * Raises a conflict/dispute for a session.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the session
     * @param request the payload containing conflict details
     * @return ResponseEntity indicating success
     */
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
