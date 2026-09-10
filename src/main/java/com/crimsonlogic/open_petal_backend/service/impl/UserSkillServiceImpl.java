package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.skill.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.dto.skill.UpdateUserSkillDto;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.repository.UserSkillRepository;
import com.crimsonlogic.open_petal_backend.service.UserSkillService;
import com.crimsonlogic.open_petal_backend.dto.skill.SkillVerificationResponseDto;
import com.crimsonlogic.open_petal_backend.service.GeminiAiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of the user skill service.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GeminiAiClient geminiAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UserSkill addSkillToUser(Long userId, CreateUserSkillDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id: " + userId));

        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new RecordNotFoundException("Skill not found with id: " + dto.getSkillId()));

        if (userSkillRepository.existsByUserIdAndSkillId(userId, dto.getSkillId())) {
            throw new IllegalArgumentException("User has already listed this skill in their teaching profile.");
        }

        UserSkill userSkill = UserSkill.builder()
                .user(user)
                .skill(skill)
                .skillLevel(dto.getSkillLevel())
                .experienceYears(dto.getExperienceYears() != null ? dto.getExperienceYears() : 0)
                .teachingMode(dto.getTeachingMode() != null ? dto.getTeachingMode() : TeachingMode.ONLINE)
                .sessionDurationMin(60)
                .creditsPerSession(dto.getCreditsPerSession() != null ? dto.getCreditsPerSession() : 10)
                .learnersTaughtCount(0)
                .avgSkillRating(BigDecimal.valueOf(0.00))
                .description(dto.getDescription())
                .build();

        return userSkillRepository.save(userSkill);
    }

    @Override
    public UserSkill updateUserSkill(Long userId, Long skillId, UpdateUserSkillDto dto) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new RecordNotFoundException("Skill offering not found for user id " + userId + " and skill id " + skillId));

        if (dto.getSkillLevel() != null) {
            userSkill.setSkillLevel(dto.getSkillLevel());
        }
        if (dto.getExperienceYears() != null) {
            userSkill.setExperienceYears(dto.getExperienceYears());
        }
        if (dto.getTeachingMode() != null) {
            userSkill.setTeachingMode(dto.getTeachingMode());
        }
        if (dto.getCreditsPerSession() != null) {
            userSkill.setCreditsPerSession(dto.getCreditsPerSession());
        }
        if (dto.getDescription() != null) {
            userSkill.setDescription(dto.getDescription());
        }

        return userSkillRepository.save(userSkill);
    }

    @Override
    public void removeSkillFromUser(Long userId, Long skillId) {
        if (!userSkillRepository.existsByUserIdAndSkillId(userId, skillId)) {
            throw new RecordNotFoundException("Skill offering not found for user id " + userId + " and skill id " + skillId);
        }
        userSkillRepository.deleteByUserIdAndSkillId(userId, skillId);
    }

    /**
     * Data Flow: This method performs input/state validation and retrieves the entity from the database.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserSkill> getAllSkillsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RecordNotFoundException("User not found with id: " + userId);
        }
        return userSkillRepository.findByUserId(userId);
    }

    /**
     * Data Flow: This method retrieves the entity from the database.
     */
    @Override
    @Transactional(readOnly = true)
    public UserSkill getUserSkillDetails(Long userId, Long skillId) {
        return userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new RecordNotFoundException("Skill offering not found for user id " + userId + " and skill id " + skillId));
    }

    @Override
    @Transactional
    public String getOrGenerateVerificationQuestions(Long userId, Long skillId) {
        UserSkill userSkill = getUserSkillDetails(userId, skillId);

        if (userSkill.getTestQuestions() != null && !userSkill.getTestQuestions().isEmpty()) {
            return userSkill.getTestQuestions();
        }

        String systemPrompt = "You are an expert examiner. Generate exactly 5 multiple choice questions (MCQ) to test a candidate's knowledge. Return ONLY a valid JSON array of exactly 5 objects. Each object MUST have 'question' (string), 'options' (array of exactly 4 strings), and 'correctAnswer' (string, matching one of the options). Do NOT wrap in markdown blocks, just return the raw JSON.";
        String payload = String.format("Generate 5 MCQs for the skill: '%s', at experience level: '%s', years of experience: %d", 
                userSkill.getSkill().getName(), userSkill.getSkillLevel(), userSkill.getExperienceYears());

        try {
            String rawJsonResponse = geminiAiClient.generateContent(systemPrompt, payload);
            
            // Clean markdown json ticks if any
            String cleanJson = rawJsonResponse.trim();
            if (cleanJson.startsWith("```json")) cleanJson = cleanJson.substring(7);
            else if (cleanJson.startsWith("```")) cleanJson = cleanJson.substring(3);
            if (cleanJson.endsWith("```")) cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
            cleanJson = cleanJson.trim();

            // Validate that it's a valid JSON array before saving
            objectMapper.readTree(cleanJson);

            userSkill.setTestQuestions(cleanJson);
            userSkillRepository.save(userSkill);
            return cleanJson;
        } catch (Exception e) {
            log.error("Failed to generate test questions", e);
            throw new RuntimeException("Failed to generate test questions from AI");
        }
    }

    @Override
    @Transactional
    public SkillVerificationResponseDto submitVerificationAnswers(Long userId, Long skillId, List<String> answers) {
        UserSkill userSkill = getUserSkillDetails(userId, skillId);
        
        if (userSkill.getTestQuestions() == null || userSkill.getTestQuestions().isEmpty()) {
            throw new IllegalStateException("Test questions have not been generated yet.");
        }

        if (answers == null || answers.size() != 5) {
            throw new IllegalArgumentException("Exactly 5 answers must be provided.");
        }

        int correctCount = 0;
        try {
            List<Map<String, Object>> questions = objectMapper.readValue(userSkill.getTestQuestions(), new TypeReference<List<Map<String, Object>>>() {});
            
            for (int i = 0; i < 5; i++) {
                String correctAnswer = (String) questions.get(i).get("correctAnswer");
                String userAnswer = answers.get(i);
                if (correctAnswer != null && correctAnswer.equals(userAnswer)) {
                    correctCount++;
                }
            }

            // Save user answers as JSON
            userSkill.setTestAnswers(objectMapper.writeValueAsString(answers));
            
            int marks = (correctCount * 100) / 5;
            userSkill.setTestMarks(marks);

            if (marks == 100) {
                userSkill.setIsVerified(true);
            }

            userSkillRepository.save(userSkill);

            return SkillVerificationResponseDto.builder()
                    .status(marks == 100 ? "PASS" : "FAIL")
                    .marks(marks)
                    .build();
        } catch (Exception e) {
            log.error("Failed to evaluate test answers", e);
            throw new RuntimeException("Failed to evaluate test answers");
        }
    }
}
