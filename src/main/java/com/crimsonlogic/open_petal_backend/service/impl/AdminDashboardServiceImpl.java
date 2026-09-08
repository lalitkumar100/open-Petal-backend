package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminDashboardDto;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.SessionConflictRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillCategoryRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserQueryRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.service.AdminDashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillCategoryRepository skillCategoryRepository;
    private final SessionConflictRepository conflictRepository;
    private final LearningSessionRepository sessionRepository;
    private final UserQueryRepository queryRepository;

    public AdminDashboardServiceImpl(
            UserRepository userRepository,
            SkillRepository skillRepository,
            SkillCategoryRepository skillCategoryRepository,
            SessionConflictRepository conflictRepository,
            LearningSessionRepository sessionRepository,
            UserQueryRepository queryRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.skillCategoryRepository = skillCategoryRepository;
        this.conflictRepository = conflictRepository;
        this.sessionRepository = sessionRepository;
        this.queryRepository = queryRepository;
    }

    @Override
    public AdminDashboardDto getDashboardStats() {
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);

        long totalUsers = userRepository.count();
        long newUsers24h = userRepository.countByCreatedAtAfter(last24h);

        long totalSkills = skillRepository.count();
        long totalSkillCategories = skillCategoryRepository.count();

        long totalConflicts = conflictRepository.count();

        long totalSessions = sessionRepository.count();
        long sessions24h = sessionRepository.countByCreatedAtAfter(last24h);

        long newQueries24h = queryRepository.countByCreatedAtAfter(last24h);

        return AdminDashboardDto.builder()
                .totalUsers(totalUsers)
                .newUsers24h(newUsers24h)
                .totalSkills(totalSkills)
                .totalSkillCategories(totalSkillCategories)
                .totalConflicts(totalConflicts)
                .totalSessions(totalSessions)
                .sessions24h(sessions24h)
                .newQueries24h(newQueries24h)
                .build();
    }
}
