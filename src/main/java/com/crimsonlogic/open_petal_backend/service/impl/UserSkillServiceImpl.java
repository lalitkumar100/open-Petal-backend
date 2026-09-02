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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<UserSkill> getAllSkillsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RecordNotFoundException("User not found with id: " + userId);
        }
        return userSkillRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSkill getUserSkillDetails(Long userId, Long skillId) {
        return userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new RecordNotFoundException("Skill offering not found for user id " + userId + " and skill id " + skillId));
    }
}