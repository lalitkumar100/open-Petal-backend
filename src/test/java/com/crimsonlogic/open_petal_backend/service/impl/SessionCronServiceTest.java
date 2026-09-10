package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionCronService Mockito Unit Tests")
class SessionCronServiceTest {

    @Mock
    private LearningSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionCronService sessionCronService;

    /**
     * Purpose: Tests auto-cancellation of past scheduled sessions and refund of learner credits.
     * Preconditions: Session is SCHEDULED, start time was over 15 minutes ago, session is CREDIT based.
     * Expected Behavior: Sets session status to CANCELLED, refunds creditsHeld to learner, saves learner and session.
     */
    @Test
    @DisplayName("Should auto-cancel expired SCHEDULED sessions and refund credits to learner")
    void givenExpiredScheduledSession_whenHandleExpirations_thenAutoCancelsAndRefundsLearner() {
        // Arrange
        User learner = new User();
        learner.setId(10L);
        learner.setCredit(50);

        LearningSession session = LearningSession.builder()
                .id(100L)
                .learner(learner)
                .sessionDate(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .status(SessionStatus.SCHEDULED)
                .sessionType(SessionType.CREDIT)
                .creditsHeld(15)
                .build();

        given(sessionRepository.findByStatusAndIsDeletedFalse(SessionStatus.SCHEDULED)).willReturn(List.of(session));
        given(sessionRepository.findByStatusAndIsDeletedFalse(SessionStatus.STARTED)).willReturn(List.of());

        // Act
        sessionCronService.handleSessionExpirations();

        // Assert
        assertThat(session.getStatus()).isEqualTo(SessionStatus.CANCELLED);
        assertThat(session.getCreditsHeld()).isEqualTo(0);
        assertThat(learner.getCredit()).isEqualTo(65); // 50 + 15

        verify(userRepository, times(1)).save(learner);
        verify(sessionRepository, times(1)).save(session);
    }

    /**
     * Purpose: Tests auto-completion of past started sessions and release of credits to mentor.
     * Preconditions: Session is STARTED, end time was over 15 minutes ago, session is CREDIT based.
     * Expected Behavior: Sets status to ENDED, awards credits to mentor, saves mentor and session.
     */
    @Test
    @DisplayName("Should auto-complete expired STARTED sessions and release credits to mentor")
    void givenExpiredStartedSession_whenHandleExpirations_thenAutoCompletesAndPaysMentor() {
        // Arrange
        User mentor = new User();
        mentor.setId(20L);
        mentor.setCredit(100);

        LearningSession session = LearningSession.builder()
                .id(200L)
                .mentor(mentor)
                .sessionDate(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .status(SessionStatus.STARTED)
                .sessionType(SessionType.CREDIT)
                .creditsHeld(20)
                .build();

        given(sessionRepository.findByStatusAndIsDeletedFalse(SessionStatus.SCHEDULED)).willReturn(List.of());
        given(sessionRepository.findByStatusAndIsDeletedFalse(SessionStatus.STARTED)).willReturn(List.of(session));

        // Act
        sessionCronService.handleSessionExpirations();

        // Assert
        assertThat(session.getStatus()).isEqualTo(SessionStatus.ENDED);
        assertThat(session.getCreditsHeld()).isEqualTo(0);
        assertThat(mentor.getCredit()).isEqualTo(120); // 100 + 20

        verify(userRepository, times(1)).save(mentor);
        verify(sessionRepository, times(1)).save(session);
    }
}
