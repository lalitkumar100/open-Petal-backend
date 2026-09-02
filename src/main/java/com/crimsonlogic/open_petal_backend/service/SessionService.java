package com.skillexchange.service;


import com.crimsonlogic.open_petal_backend.dto.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;

public interface SessionService {
    LearningSession createSession(CreateSessionDto dto);
    LearningSession startSession(Long sessionId, Long mentorId, String otp);
    LearningSession completeSession(Long sessionId, Long learnerId, String otp);
}