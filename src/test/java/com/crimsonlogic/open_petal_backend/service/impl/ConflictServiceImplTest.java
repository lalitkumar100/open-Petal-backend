package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.conflict.UserConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.SessionConflict;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import com.crimsonlogic.open_petal_backend.repository.SessionConflictRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConflictServiceImplTest {

    @Mock
    private SessionConflictRepository sessionConflictRepository;

    @InjectMocks
    private ConflictServiceImpl conflictService;

    @Test
    @DisplayName("Get my conflicts successfully as Learner")
    void getMyConflicts_asLearner() {
        User learner = new User();
        learner.setId(10L);
        learner.setFirstName("Learner");
        learner.setLastName("User");

        User mentor = new User();
        mentor.setId(20L);
        mentor.setFirstName("Mentor");
        mentor.setLastName("User");

        Skill skill = new Skill();
        skill.setName("Java");

        LearningSession session = new LearningSession();
        session.setId(1L);
        session.setLearner(learner);
        session.setMentor(mentor);
        session.setPrimarySkill(skill);

        SessionConflict conflict = new SessionConflict();
        conflict.setId(100L);
        conflict.setSession(session);
        conflict.setRaisedBy(learner);
        conflict.setStatus(ConflictStatus.OPEN);
        conflict.setReason(com.crimsonlogic.open_petal_backend.enums.ConflictReason.NO_SHOW);
        conflict.setCreatedAt(LocalDateTime.now());

        given(sessionConflictRepository.findAllMyConflicts(10L)).willReturn(List.of(conflict));

        List<UserConflictSummaryResponseDto> results = conflictService.getMyConflicts(10L);

        assertNotNull(results);
        assertEquals(1, results.size());
        
        UserConflictSummaryResponseDto dto = results.get(0);
        assertEquals(100L, dto.getConflictId());
        assertEquals("Mentor User", dto.getPartnerName());
        assertEquals("LEARNER", dto.getMyRole());
        assertEquals("Java", dto.getPrimarySkillName());
        assertTrue(dto.isRaisedByMe());
        assertEquals(ConflictStatus.OPEN, dto.getStatus());
    }

    @Test
    @DisplayName("Get my conflicts successfully as Mentor")
    void getMyConflicts_asMentor() {
        User learner = new User();
        learner.setId(10L);
        learner.setFirstName("Learner");
        learner.setLastName("User");

        User mentor = new User();
        mentor.setId(20L);
        mentor.setFirstName("Mentor");
        mentor.setLastName("User");

        Skill skill = new Skill();
        skill.setName("Python");

        LearningSession session = new LearningSession();
        session.setId(2L);
        session.setLearner(learner);
        session.setMentor(mentor);
        session.setPrimarySkill(skill);

        SessionConflict conflict = new SessionConflict();
        conflict.setId(200L);
        conflict.setSession(session);
        conflict.setRaisedBy(learner); // Learner raised it
        conflict.setStatus(ConflictStatus.UNDER_REVIEW);
        conflict.setReason(com.crimsonlogic.open_petal_backend.enums.ConflictReason.QUALITY);
        conflict.setCreatedAt(LocalDateTime.now());

        given(sessionConflictRepository.findAllMyConflicts(20L)).willReturn(List.of(conflict));

        List<UserConflictSummaryResponseDto> results = conflictService.getMyConflicts(20L);

        assertNotNull(results);
        assertEquals(1, results.size());
        
        UserConflictSummaryResponseDto dto = results.get(0);
        assertEquals(200L, dto.getConflictId());
        assertEquals("Learner User", dto.getPartnerName());
        assertEquals("MENTOR", dto.getMyRole());
        assertEquals("Python", dto.getPrimarySkillName());
        assertFalse(dto.isRaisedByMe()); // Raised by learner, we are mentor
        assertEquals(ConflictStatus.UNDER_REVIEW, dto.getStatus());
    }
}
