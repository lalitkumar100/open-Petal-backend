package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.admin.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.dto.skill.SkillDetailResponseDto;
import org.springframework.data.domain.Page;
import java.util.List;

public interface SkillService {
    ApiResponse.SkillDTO createSkill(ApiResponse.SkillDTO skillDTO);
    ApiResponse.SkillDTO addSkillToCategory(AddSkillRequestDto requestDto);
    ApiResponse.SkillDTO updateSkill(Long id, ApiResponse.SkillDTO skillDTO);
    ApiResponse.SkillDTO getSkillById(Long id);
    List<ApiResponse.SkillDTO> getAllSkills();
    Page<ApiResponse.SkillDTO> getSkillsByCategory(Long categoryId, int page, int size, String searchQuery);
    void deleteSkill(Long id);
    SkillDetailResponseDto getAdminSkillDetails(Long skillId);
}