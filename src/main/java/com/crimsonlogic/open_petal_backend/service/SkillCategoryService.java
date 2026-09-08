package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for skill category operations.
 */
public interface SkillCategoryService {

    /**
     * Create Category.
     */
    SkillCategory createCategory(SkillCategoryRequestDto dto);

    /**
     * Update Category.
     */
    SkillCategory updateCategory(Long id, SkillCategoryRequestDto dto);

    /**
     * Get All Categories.
     */
    Page<SkillCategory> getAllCategories(int page, int size, String searchQuery);

    /**
     * Get All Categories.
     */
    List<SkillCategory> getAllCategories();

    /**
     * Get Category By Id.
     */
    SkillCategory getCategoryById(Long id);
}
