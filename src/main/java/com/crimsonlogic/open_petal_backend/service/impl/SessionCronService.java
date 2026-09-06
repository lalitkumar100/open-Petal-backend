package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionCronService {

    private final LearningSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 * * * * *") // Every minute
    @Transactional
    public void handleSessionExpirations() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Find SCHEDULED sessions where now > startTime + 15 mins -> CANCELLED
        List<LearningSession> scheduledSessions = sessionRepository.findByStatusAndIsDeletedFalse(SessionStatus.SCHEDULED);
        for (LearningSession session : scheduledSessions) {
            LocalDateTime windowEnd = session.getSessionDate().atTime(session.getStartTime()).plusMinutes(15);
            if (now.isAfter(windowEnd)) {
                // Auto cancel
                if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
                    User learner = session.getLearner();
                    learner.setCredit(learner.getCredit() + session.getCreditsHeld());
                    session.setCreditsHeld(0);
                    userRepository.save(learner);
                }
                session.setStatus(SessionStatus.CANCELLED);
                sessionRepository.save(session);
                System.out.println("Auto-cancelled session " + session.getId());
            }
        }

        // 2. Find STARTED sessions where now > endTime + 15 mins -> ENDED
        List<LearningSession> startedSessions = sessionRepository.findByStatusAndIsDeletedFalse(SessionStatus.STARTED);
        for (LearningSession session : startedSessions) {
            LocalDateTime windowEnd = session.getSessionDate().atTime(session.getEndTime()).plusMinutes(15);
            if (now.isAfter(windowEnd)) {
                // Auto complete
                session.setStatus(SessionStatus.ENDED);
                if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
                    User mentor = session.getMentor();
                    mentor.setCredit(mentor.getCredit() + session.getCreditsHeld());
                    session.setCreditsHeld(0);
                    userRepository.save(mentor);
                }
                sessionRepository.save(session);
                System.out.println("Auto-completed session " + session.getId());
            }
        }
    }
}
