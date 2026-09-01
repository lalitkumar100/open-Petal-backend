package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;

import java.util.List;

public interface SkillCategoryService {

    SkillCategory createCategory(SkillCategoryRequestDto dto);

    SkillCategory updateCategory(Long id, SkillCategoryRequestDto dto);

    List<SkillCategory> getAllCategories();

    SkillCategory getCategoryById(Long id);
}