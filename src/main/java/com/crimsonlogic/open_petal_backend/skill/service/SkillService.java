package com.crimsonlogic.open_petal_backend.skill.service;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillResponseDto;

import com.crimsonlogic.open_petal_backend.admin.dto.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillDetailResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;

public interface SkillService {
    SkillResponseDto createSkill(SkillResponseDto skillDTO);
    SkillResponseDto addSkillToCategory(AddSkillRequestDto requestDto);
    SkillResponseDto updateSkill(Long id, SkillResponseDto skillDTO);
    SkillResponseDto getSkillById(Long id);
    List<SkillResponseDto> getAllSkills();
    Page<SkillResponseDto> getSkillsByCategory(Long categoryId, int page, int size, String searchQuery);
    void deleteSkill(Long id);
    SkillDetailResponseDto getAdminSkillDetails(Long skillId);
}
