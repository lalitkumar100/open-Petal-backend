package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningGoalRepository extends JpaRepository<LearningGoal, Long> {
    List<LearningGoal> findByUserId(Long userId);
    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
    boolean existsByUserIdAndSkillIdAndIsDeletedFalse(Long userId, Long skillId);
    Optional<LearningGoal> findByUserIdAndSkillId(Long userId, Long skillId);
    long countBySkillIdAndIsDeletedFalseAndUserLoginIsDeletedFalse(Long skillId);
}
