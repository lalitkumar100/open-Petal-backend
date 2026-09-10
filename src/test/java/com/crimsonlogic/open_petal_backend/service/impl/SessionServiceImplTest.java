package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.session.SessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionServiceImpl Tests")
class SessionServiceImplTest {

    @Mock
    private LearningSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    @DisplayName("Should successfully retrieve session details by ID")
    void givenValidSessionId_whenGetSessionDetails_thenReturnsSession() {
        User learner = User.builder().id(10L).firstName("Alice").build();
        User mentor = User.builder().id(20L).firstName("Bob").build();

        LearningSession session = LearningSession.builder()
                .id(50L)
                .learner(learner)
                .mentor(mentor)
                .status(SessionStatus.REQUESTED)
                .creditsHeld(10)
                .build();

        given(sessionRepository.findSessionWithDetailsById(50L)).willReturn(Optional.of(session));

        SessionDetailResponseDto result = sessionService.getSessionDetails(50L, 10L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(50L);
        assertThat(result.getStatus()).isEqualTo(SessionStatus.REQUESTED);
    }

    @Test
    @DisplayName("Should throw RecordNotFoundException when session does not exist")
    void givenInvalidSessionId_whenGetSessionDetails_thenThrowsException() {
        given(sessionRepository.findSessionWithDetailsById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSessionDetails(999L, 10L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Session not found");
    }
}
