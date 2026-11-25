package com.crimsonlogic.open_petal_backend.skill.service;

import com.crimsonlogic.open_petal_backend.skill.dto.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.skill.dto.UpdateUserSkillDto;
import com.crimsonlogic.open_petal_backend.skill.entity.UserSkill;
import java.util.List;
import org.springframework.stereotype.Service;

public interface UserSkillService {

    UserSkill addSkillToUser(Long userId, CreateUserSkillDto dto);

    UserSkill updateUserSkill(Long userId, Long skillId, UpdateUserSkillDto dto);

    void removeSkillFromUser(Long userId, Long skillId);

    List<UserSkill> getAllSkillsByUser(Long userId);

    UserSkill getUserSkillDetails(Long userId, Long skillId);
}
