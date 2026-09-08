package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LearningGoalRepository;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import com.crimsonlogic.open_petal_backend.service.GeminiAiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;

import java.util.List;
import java.util.Map;

/**
 * Implementation of the learning goal service.
 */
@Service
public class LearningGoalServiceImpl implements LearningGoalService {

    private final LearningGoalRepository learningGoalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GeminiAiClient geminiAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public LearningGoalServiceImpl(LearningGoalRepository learningGoalRepository,
                                   UserRepository userRepository,
                                   SkillRepository skillRepository,
                                   GeminiAiClient geminiAiClient) {
        this.learningGoalRepository = learningGoalRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.geminiAiClient = geminiAiClient;
    }

    @Override
    public LearningGoal createLearningGoal(Long userId, Long skillId, SkillLevel currentLevel, SkillLevel targetLevel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RecordNotFoundException("Skill not found with id " + skillId));

        java.util.Optional<LearningGoal> existingGoalOpt = learningGoalRepository.findByUserIdAndSkillId(userId, skillId);
        if (existingGoalOpt.isPresent()) {
            LearningGoal existingGoal = existingGoalOpt.get();
            if (Boolean.FALSE.equals(existingGoal.getIsDeleted())) {
                throw new com.crimsonlogic.open_petal_backend.exception.CustomException(
                        "You already have an active learning goal for this skill.", 
                        org.springframework.http.HttpStatus.CONFLICT);
            } else {
                existingGoal.setIsDeleted(false);
                existingGoal.setCurrentLevel(currentLevel);
                existingGoal.setTargetLevel(targetLevel);
                existingGoal.setRoadplan(null);
                return learningGoalRepository.save(existingGoal);
            }
        }

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

    @Override
    public LearningGoal generateRoadplan(Long id) {
        LearningGoal goal = getLearningGoalById(id);
        
        String systemPrompt = "You are an expert learning pathway creator. Your goal is to generate a structured JSON roadmap for a learner. "
            + "The JSON MUST STRICTLY adhere to the following schema and return ONLY the JSON (no markdown formatting, no backticks, no explanations):\n"
            + "{\n"
            + "  \"title\": \"String\",\n"
            + "  \"subtitle\": \"String\",\n"
            + "  \"milestones\": [\n"
            + "    {\n"
            + "      \"id\": \"String (unique UUID or identifier)\",\n"
            + "      \"step\": \"String (e.g., 'Step 1', 'Phase 1')\",\n"
            + "      \"title\": \"String\",\n"
            + "      \"desc\": \"String\",\n"
            + "      \"checkpoint\": \"String (a task or project to verify this milestone)\",\n"
            + "      \"nodes\": [\n"
            + "        {\n"
            + "          \"id\": \"String (unique UUID or identifier)\",\n"
            + "          \"title\": \"String\",\n"
            + "          \"desc\": \"String\",\n"
            + "          \"completed\": false,\n"
            + "          \"resources\": [\n"
            + "            { \"label\": \"String\", \"url\": \"String\" }\n"
            + "          ]\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

        String payload = String.format("Create a learning roadmap for the skill '%s'. The user is currently at a '%s' level and wants to reach an '%s' level.", 
                goal.getSkill().getName(), goal.getCurrentLevel().name(), goal.getTargetLevel().name());
                
        try {
            String jsonResponse = geminiAiClient.generateContent(systemPrompt, payload);
            
            // Clean markdown if present
            if (jsonResponse.startsWith("```json")) {
                jsonResponse = jsonResponse.substring(7);
            }
            if (jsonResponse.startsWith("```")) {
                jsonResponse = jsonResponse.substring(3);
            }
            if (jsonResponse.endsWith("```")) {
                jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
            }
            jsonResponse = jsonResponse.trim();
            
            // Parse and validate
            Map<String, Object> roadplanData = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
            
            goal.setRoadplan(roadplanData);
            return learningGoalRepository.save(goal);
        } catch (Exception e) {
            throw new com.crimsonlogic.open_petal_backend.exception.CustomException("Failed to generate and parse roadmap: " + e.getMessage(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
