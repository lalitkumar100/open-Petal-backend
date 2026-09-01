package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LearningGoalRepository;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enumerator.SkillLevel;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
public class LearningGoalServiceImpl implements LearningGoalService {

    private final LearningGoalRepository learningGoalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Autowired
    public LearningGoalServiceImpl(LearningGoalRepository learningGoalRepository,
                                   UserRepository userRepository,
                                   SkillRepository skillRepository) {
        this.learningGoalRepository = learningGoalRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public LearningGoal createLearningGoal(Long userId, Long skillId, SkillLevel currentLevel, SkillLevel targetLevel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RecordNotFoundException("Skill not found with id " + skillId));

        LearningGoal goal = LearningGoal.builder()
                .user(user)
                .skill(skill)
                .currentLevel(currentLevel)
                .targetLevel(targetLevel)
                .build();
        return learningGoalRepository.save(goal);
    }

    @Override
    public LearningGoal getLearningGoalById(Long id) {
        return learningGoalRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Learning goal not found with id " + id));
    }

    @Override
    public List<LearningGoal> getLearningGoalsByUserId(Long userId) {
        return learningGoalRepository.findByUserId(userId);
    }

    @Override
    public LearningGoal updateLearningGoal(Long id, SkillLevel currentLevel, SkillLevel targetLevel) {
        LearningGoal goal = getLearningGoalById(id);
        goal.setCurrentLevel(currentLevel);
        goal.setTargetLevel(targetLevel);
        return learningGoalRepository.save(goal);
    }

    @Override
    public void deleteLearningGoal(Long id) {
        learningGoalRepository.deleteById(id);
    }

    @Override
    public LearningGoal updateRoadplan(Long id, Map<String, Object> roadplan) {
        LearningGoal goal = getLearningGoalById(id);
        goal.setRoadplan(roadplan);
        return learningGoalRepository.save(goal);
    }
}
