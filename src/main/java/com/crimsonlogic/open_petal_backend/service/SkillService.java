package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;

import java.util.List;

public interface SkillService {
    ApiResponse.SkillDTO createSkill(ApiResponse.SkillDTO skillDTO);
    ApiResponse.SkillDTO updateSkill(Long id, ApiResponse.SkillDTO skillDTO);
    ApiResponse.SkillDTO getSkillById(Long id);
    List<ApiResponse.SkillDTO> getAllSkills();
    void deleteSkill(Long id);
}