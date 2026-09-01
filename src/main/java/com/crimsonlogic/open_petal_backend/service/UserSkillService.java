package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.skill.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.dto.skill.UpdateUserSkillDto;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;

import java.util.List;

public interface UserSkillService {

    UserSkill addSkillToUser(Long userId, CreateUserSkillDto dto);

    UserSkill updateUserSkill(Long userId, Long skillId, UpdateUserSkillDto dto);

    void removeSkillFromUser(Long userId, Long skillId);

    List<UserSkill> getAllSkillsByUser(Long userId);

    UserSkill getUserSkillDetails(Long userId, Long skillId);
}