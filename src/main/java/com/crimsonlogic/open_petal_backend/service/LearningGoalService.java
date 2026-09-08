package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import java.util.List;
import java.util.Map;

/**
 * Service interface for learning goal operations.
 */
public interface LearningGoalService {
    /**
     * Create Learning Goal.
     */
    LearningGoal createLearningGoal(Long userId, Long skillId, SkillLevel currentLevel, SkillLevel targetLevel);
    /**
     * Get Learning Goal By Id.
     */
    LearningGoal getLearningGoalById(Long id);
    /**
     * Get Learning Goals By User Id.
     */
    List<LearningGoal> getLearningGoalsByUserId(Long userId);
    /**
     * Update Learning Goal.
     */
    LearningGoal updateLearningGoal(Long id, SkillLevel currentLevel, SkillLevel targetLevel);
    /**
     * Delete Learning Goal.
     */
    void deleteLearningGoal(Long id);
    /**
     * Update Roadplan.
     */
    LearningGoal updateRoadplan(Long id, Map<String, Object> roadplan);
    /**
     * Generate Roadplan via AI.
     */
    LearningGoal generateRoadplan(Long id);
}
