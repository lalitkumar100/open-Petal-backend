package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.skill.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.dto.skill.UpdateUserSkillDto;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service interface for user skill operations.
 */
public interface UserSkillService {

    /**
     * Add Skill To User.
     */
    UserSkill addSkillToUser(Long userId, CreateUserSkillDto dto);

    /**
     * Update User Skill.
     */
    UserSkill updateUserSkill(Long userId, Long skillId, UpdateUserSkillDto dto);

    /**
     * Remove Skill From User.
     */
    void removeSkillFromUser(Long userId, Long skillId);

    /**
     * Get All Skills By User.
     */
    List<UserSkill> getAllSkillsByUser(Long userId);

    /**
     * Get User Skill Details.
     */
    UserSkill getUserSkillDetails(Long userId, Long skillId);
}
