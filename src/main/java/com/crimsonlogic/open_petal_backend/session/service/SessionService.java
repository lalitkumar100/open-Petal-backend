package com.crimsonlogic.open_petal_backend.session.service;

import com.crimsonlogic.open_petal_backend.admin.dto.AdminSessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.admin.dto.AdminSessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.session.dto.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionConflictRequestDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionOtpRequestDto;
import com.crimsonlogic.open_petal_backend.session.dto.SessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.session.entity.LearningSession;

public interface SessionService {
    LearningSession createSession(Long learnerId, CreateSessionDto dto);
    LearningSession startSession(Long sessionId, Long mentorId, String otp);
    LearningSession completeSession(Long sessionId, Long learnerId, String otp);

    java.util.List<com.crimsonlogic.open_petal_backend.session.dto.SessionSummaryResponseDto> getMySessions(Long userId);
    com.crimsonlogic.open_petal_backend.session.dto.SessionDetailResponseDto getSessionDetails(Long sessionId, Long userId);
    void cancelSession(Long sessionId, Long userId);
    void acceptSession(Long sessionId, Long userId);
    void declineSession(Long sessionId, Long userId);

    String getMyOtp(Long sessionId, Long userId);
    void verifyStart(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.session.dto.SessionOtpRequestDto dto);

    void verifyEnd(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.session.dto.SessionOtpRequestDto dto);
    void raiseConflict(Long sessionId, Long userId, com.crimsonlogic.open_petal_backend.session.dto.SessionConflictRequestDto dto);
    com.crimsonlogic.open_petal_backend.session.dto.SessionConflictDetailResponseDto getSessionConflict(Long sessionId, Long userId);
    void submitConflictStory(Long sessionId, Long userId, String story);

    java.util.List<com.crimsonlogic.open_petal_backend.admin.dto.AdminSessionSummaryResponseDto> getAllGlobalSessions();
    com.crimsonlogic.open_petal_backend.admin.dto.AdminSessionDetailResponseDto getAdminSessionDetails(Long sessionId);
    void adminCancelSession(Long sessionId, String reason);
}
