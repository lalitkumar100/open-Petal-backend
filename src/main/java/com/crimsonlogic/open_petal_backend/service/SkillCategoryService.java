package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SkillCategoryService {

    SkillCategory createCategory(SkillCategoryRequestDto dto);

    SkillCategory updateCategory(Long id, SkillCategoryRequestDto dto);

    Page<SkillCategory> getAllCategories(int page, int size, String searchQuery);

    List<SkillCategory> getAllCategories();

    SkillCategory getCategoryById(Long id);
}