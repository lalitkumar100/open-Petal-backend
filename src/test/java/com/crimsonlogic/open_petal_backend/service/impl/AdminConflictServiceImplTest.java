package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.ResolveConflictRequestDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.SessionConflict;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.SessionConflictRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminConflictServiceImplTest {

    @Mock
    private SessionConflictRepository conflictRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LearningSessionRepository sessionRepository;

    @InjectMocks
    private AdminConflictServiceImpl adminConflictService;

    private User admin;
    private User learner;
    private User mentor;
    private Skill skill;
    private LearningSession session;
    private SessionConflict conflict;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setFirstName("Admin");
        admin.setLastName("User");

        learner = new User();
        learner.setId(2L);
        learner.setFirstName("Learner");
        learner.setLastName("One");
        learner.setCredit(100);

        mentor = new User();
        mentor.setId(3L);
        mentor.setFirstName("Mentor");
        mentor.setLastName("One");
        mentor.setCredit(100);

        skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        session = new LearningSession();
        session.setId(10L);
        session.setLearner(learner);
        session.setMentor(mentor);
        session.setPrimarySkill(skill);
        session.setSessionType(SessionType.CREDIT);
        session.setCreditsHeld(50);
        session.setStatus(SessionStatus.SCHEDULED);

        conflict = new SessionConflict();
        conflict.setId(100L);
        conflict.setSession(session);
        conflict.setStatus(ConflictStatus.OPEN);
        conflict.setRaisedBy(learner);
        conflict.setReason(com.crimsonlogic.open_petal_backend.enums.ConflictReason.NO_SHOW);
    }

    @Test
    @DisplayName("Get all conflicts for admin successfully")
    void getAllConflictsForAdmin_success() {
        given(conflictRepository.findAllGlobalConflicts()).willReturn(List.of(conflict));

        List<AdminConflictSummaryResponseDto> result = adminConflictService.getAllConflictsForAdmin();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getConflictId());
        assertEquals("Learner One", result.get(0).getLearnerName());
    }

    @Test
    @DisplayName("Get conflict details successfully")
    void getAdminConflictDetails_success() {
        given(conflictRepository.findAdminConflictDetailById(100L)).willReturn(Optional.of(conflict));

        AdminConflictDetailResponseDto result = adminConflictService.getAdminConflictDetails(100L);

        assertNotNull(result);
        assertEquals(100L, result.getConflictId());
        assertEquals("Java", result.getPrimarySkillName());
    }

    @Test
    @DisplayName("Get conflict details throws RecordNotFoundException when not found")
    void getAdminConflictDetails_notFound() {
        given(conflictRepository.findAdminConflictDetailById(100L)).willReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> adminConflictService.getAdminConflictDetails(100L));
    }

    @Test
    @DisplayName("Resolve conflict - Refund Learner")
    void resolveConflict_refundLearner() {
        ResolveConflictRequestDto request = new ResolveConflictRequestDto();
        request.setResolutionAction("REFUND_LEARNER");
        request.setAdminNotes("Refunding as requested.");

        given(conflictRepository.findById(100L)).willReturn(Optional.of(conflict));
        given(userRepository.findById(1L)).willReturn(Optional.of(admin));

        adminConflictService.resolveConflict(100L, 1L, request);

        assertEquals(SessionStatus.CANCELLED, session.getStatus());
        assertEquals(ConflictStatus.RESOLVED_REFUNDED, conflict.getStatus());
        assertEquals(0, session.getCreditsHeld());
        assertEquals(150, learner.getCredit());
        assertNotNull(conflict.getResolvedAt());
        assertEquals(admin, conflict.getResolvedBy());
        verify(userRepository).save(learner);
        verify(sessionRepository).save(session);
        verify(conflictRepository).save(conflict);
    }

    @Test
    @DisplayName("Resolve conflict - Payout Mentor")
    void resolveConflict_payoutMentor() {
        ResolveConflictRequestDto request = new ResolveConflictRequestDto();
        request.setResolutionAction("PAYOUT_MENTOR");
        request.setAdminNotes("Paying mentor.");

        given(conflictRepository.findById(100L)).willReturn(Optional.of(conflict));
        given(userRepository.findById(1L)).willReturn(Optional.of(admin));

        adminConflictService.resolveConflict(100L, 1L, request);

        assertEquals(SessionStatus.ENDED, session.getStatus());
        assertEquals(ConflictStatus.RESOLVED_PAID_MENTOR, conflict.getStatus());
        assertEquals(0, session.getCreditsHeld());
        assertEquals(150, mentor.getCredit());
        verify(userRepository).save(mentor);
        verify(sessionRepository).save(session);
        verify(conflictRepository).save(conflict);
    }

    @Test
    @DisplayName("Resolve conflict - Dismiss (Auto-converts to Refund for Credit Sessions with held credits)")
    void resolveConflict_dismissConvertsToRefundForCredit() {
        ResolveConflictRequestDto request = new ResolveConflictRequestDto();
        request.setResolutionAction("DISMISS");

        given(conflictRepository.findById(100L)).willReturn(Optional.of(conflict));
        given(userRepository.findById(1L)).willReturn(Optional.of(admin));

        adminConflictService.resolveConflict(100L, 1L, request);

        // It should refund learner because it's a CREDIT session with credits > 0
        assertEquals(ConflictStatus.RESOLVED_REFUNDED, conflict.getStatus());
        assertEquals(150, learner.getCredit());
    }
    
    @Test
    @DisplayName("Resolve conflict - Dismiss (Actual Dismiss for Barter)")
    void resolveConflict_dismissActualForBarter() {
        session.setSessionType(SessionType.BARTER);
        session.setCreditsHeld(0);
        ResolveConflictRequestDto request = new ResolveConflictRequestDto();
        request.setResolutionAction("DISMISS");

        given(conflictRepository.findById(100L)).willReturn(Optional.of(conflict));
        given(userRepository.findById(1L)).willReturn(Optional.of(admin));

        adminConflictService.resolveConflict(100L, 1L, request);

        assertEquals(ConflictStatus.DISMISSED, conflict.getStatus());
        assertEquals(SessionStatus.CANCELLED, session.getStatus());
    }

    @Test
    @DisplayName("Resolve conflict - Invalid action")
    void resolveConflict_invalidAction() {
        ResolveConflictRequestDto request = new ResolveConflictRequestDto();
        request.setResolutionAction("INVALID_ACTION");

        given(conflictRepository.findById(100L)).willReturn(Optional.of(conflict));
        given(userRepository.findById(1L)).willReturn(Optional.of(admin));

        assertThrows(CustomException.class, () -> adminConflictService.resolveConflict(100L, 1L, request));
    }

    @Test
    @DisplayName("Resolve conflict - Already resolved throws exception")
    void resolveConflict_alreadyResolved() {
        conflict.setStatus(ConflictStatus.RESOLVED_REFUNDED);
        ResolveConflictRequestDto request = new ResolveConflictRequestDto();
        request.setResolutionAction("DISMISS");

        given(conflictRepository.findById(100L)).willReturn(Optional.of(conflict));

        assertThrows(CustomException.class, () -> adminConflictService.resolveConflict(100L, 1L, request));
    }
}
