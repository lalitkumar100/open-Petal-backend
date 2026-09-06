package com.crimsonlogic.open_petal_backend.service;


import com.crimsonlogic.open_petal_backend.dto.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;

public interface SessionService {
    LearningSession createSession(Long learnerId, CreateSessionDto dto);
    LearningSession startSession(Long sessionId, Long mentorId, String otp);
    LearningSession completeSession(Long sessionId, Long learnerId, String otp);

    java.util.List<com.crimsonlogic.open_petal_backend.dto.session.SessionSummaryResponseDto> getMySessions(Long userId);
    com.crimsonlogic.open_petal_backend.dto.session.SessionDetailResponseDto getSessionDetails(Long sessionId, Long userId);
    void cancelSession(Long sessionId, Long userId);
    void acceptSession(Long sessionId, Long userId);
    void declineSession(Long sessionId, Long userId);

    String getMyOtp(Long sessionId, Long userId);
    void verifyStart(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.dto.session.SessionOtpRequestDto dto);

    void verifyEnd(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.dto.session.SessionOtpRequestDto dto);
    void raiseConflict(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.dto.session.SessionConflictRequestDto dto);
    com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto getSessionConflict(Long sessionId, Long userId);
    void submitConflictStory(Long sessionId, Long userId, String story);

    java.util.List<com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionSummaryResponseDto> getAllGlobalSessions();
    com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionDetailResponseDto getAdminSessionDetails(Long sessionId);
    void adminCancelSession(Long sessionId, String reason);
}