package com.crimsonlogic.open_petal_backend.skill.service;

import com.crimsonlogic.open_petal_backend.skill.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.skill.enums.SkillLevel;
import java.util.List;
import java.util.Map;

public interface LearningGoalService {
    LearningGoal createLearningGoal(Long userId, Long skillId, SkillLevel currentLevel, SkillLevel targetLevel);
    LearningGoal getLearningGoalById(Long id);
    List<LearningGoal> getLearningGoalsByUserId(Long userId);
    LearningGoal updateLearningGoal(Long id, SkillLevel currentLevel, SkillLevel targetLevel);
    void deleteLearningGoal(Long id);
    LearningGoal updateRoadplan(Long id, Map<String, Object> roadplan);
}
