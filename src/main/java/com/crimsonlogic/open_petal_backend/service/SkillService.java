package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.admin.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.dto.skill.SkillDetailResponseDto;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * Service interface for skill operations.
 */
public interface SkillService {
    /**
     * Create Skill.
     */
    ApiResponse.SkillDTO createSkill(ApiResponse.SkillDTO skillDTO);
    /**
     * Add Skill To Category.
     */
    ApiResponse.SkillDTO addSkillToCategory(AddSkillRequestDto requestDto);
    /**
     * Update Skill.
     */
    ApiResponse.SkillDTO updateSkill(Long id, ApiResponse.SkillDTO skillDTO);
    /**
     * Get Skill By Id.
     */
    ApiResponse.SkillDTO getSkillById(Long id);
    /**
     * Get All Skills.
     */
    List<ApiResponse.SkillDTO> getAllSkills();
    /**
     * Get Skills By Category.
     */
    Page<ApiResponse.SkillDTO> getSkillsByCategory(Long categoryId, int page, int size, String searchQuery);
    /**
     * Delete Skill.
     */
    void deleteSkill(Long id);
    /**
     * Get Admin Skill Details.
     */
    SkillDetailResponseDto getAdminSkillDetails(Long skillId);
    /**
     * Get All Skill Details with pagination and search.
     */
    Page<SkillDetailResponseDto> getAllSkillDetails(int page, int size, String searchQuery);
}
