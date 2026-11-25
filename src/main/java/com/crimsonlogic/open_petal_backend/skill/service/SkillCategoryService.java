package com.crimsonlogic.open_petal_backend.skill.service;

import com.crimsonlogic.open_petal_backend.skill.dto.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.skill.entity.SkillCategory;
import java.util.List;
import org.springframework.data.domain.Page;

public interface SkillCategoryService {

    SkillCategory createCategory(SkillCategoryRequestDto dto);

    SkillCategory updateCategory(Long id, SkillCategoryRequestDto dto);

    Page<SkillCategory> getAllCategories(int page, int size, String searchQuery);

    List<SkillCategory> getAllCategories();

    SkillCategory getCategoryById(Long id);
}
