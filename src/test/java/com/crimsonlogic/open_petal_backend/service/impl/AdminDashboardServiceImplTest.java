package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminDashboardDto;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.SessionConflictRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillCategoryRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserQueryRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillCategoryRepository skillCategoryRepository;
    @Mock
    private SessionConflictRepository conflictRepository;
    @Mock
    private LearningSessionRepository sessionRepository;
    @Mock
    private UserQueryRepository queryRepository;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    @Test
    @DisplayName("Get dashboard stats successfully")
    void getDashboardStats_success() {
        given(userRepository.count()).willReturn(100L);
        given(userRepository.countByCreatedAtAfter(any())).willReturn(10L);
        
        given(skillRepository.count()).willReturn(50L);
        given(skillCategoryRepository.count()).willReturn(5L);
        
        given(conflictRepository.count()).willReturn(2L);
        
        given(sessionRepository.count()).willReturn(200L);
        given(sessionRepository.countByCreatedAtAfter(any())).willReturn(20L);
        
        given(queryRepository.countByCreatedAtAfter(any())).willReturn(15L);

        AdminDashboardDto result = adminDashboardService.getDashboardStats();

        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(10L, result.getNewUsers24h());
        assertEquals(50L, result.getTotalSkills());
        assertEquals(5L, result.getTotalSkillCategories());
        assertEquals(2L, result.getTotalConflicts());
        assertEquals(200L, result.getTotalSessions());
        assertEquals(20L, result.getSessions24h());
        assertEquals(15L, result.getNewQueries24h());
    }
}
