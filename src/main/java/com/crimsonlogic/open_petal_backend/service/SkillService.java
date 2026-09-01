package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.SkillDTO;
import java.util.List;

public interface SkillService {
    SkillDTO createSkill(SkillDTO skillDTO);
    SkillDTO updateSkill(Long id, SkillDTO skillDTO);
    SkillDTO getSkillById(Long id);
    List<SkillDTO> getAllSkills();
    void deleteSkill(Long id);
}