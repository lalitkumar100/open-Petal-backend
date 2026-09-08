package com.crimsonlogic.open_petal_backend.service;


import com.crimsonlogic.open_petal_backend.dto.session.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;

/**
 * Service interface for session operations.
 */
public interface SessionService {
    /**
     * Create Session.
     */
    LearningSession createSession(Long learnerId, CreateSessionDto dto);
    /**
     * Start Session.
     */
    LearningSession startSession(Long sessionId, Long mentorId, String otp);
    /**
     * Complete Session.
     */
    LearningSession completeSession(Long sessionId, Long learnerId, String otp);

    /**
     * Get My Sessions.
     */
    java.util.List<com.crimsonlogic.open_petal_backend.dto.session.SessionSummaryResponseDto> getMySessions(Long userId);
    /**
     * Get Session Details.
     */
    com.crimsonlogic.open_petal_backend.dto.session.SessionDetailResponseDto getSessionDetails(Long sessionId, Long userId);
    /**
     * Cancel Session.
     */
    void cancelSession(Long sessionId, Long userId);
    /**
     * Accept Session.
     */
    void acceptSession(Long sessionId, Long userId);
    /**
     * Decline Session.
     */
    void declineSession(Long sessionId, Long userId);

    /**
     * Get My Otp.
     */
    String getMyOtp(Long sessionId, Long userId);
    /**
     * Verify Start.
     */
    void verifyStart(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.dto.session.SessionOtpRequestDto dto);

    /**
     * Verify End.
     */
    void verifyEnd(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.dto.session.SessionOtpRequestDto dto);
    /**
     * Raise Conflict.
     */
    void raiseConflict(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.dto.session.SessionConflictRequestDto dto);
    /**
     * Get Session Conflict.
     */
    com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto getSessionConflict(Long sessionId, Long userId);
    /**
     * Submit Conflict Story.
     */
    void submitConflictStory(Long sessionId, Long userId, String story);

    /**
     * Get All Global Sessions.
     */
    java.util.List<com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionSummaryResponseDto> getAllGlobalSessions();
    /**
     * Get Admin Session Details.
     */
    com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionDetailResponseDto getAdminSessionDetails(Long sessionId);
    /**
     * Admin Cancel Session.
     */
    void adminCancelSession(Long sessionId, String reason);
}
