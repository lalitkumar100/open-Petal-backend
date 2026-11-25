package com.crimsonlogic.open_petal_backend.skill.service;

import com.crimsonlogic.open_petal_backend.common.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.skill.entity.Skill;
import com.crimsonlogic.open_petal_backend.skill.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.skill.repository.SkillCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillCategoryServiceImpl implements SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;

    @Override
    public SkillCategory createCategory(SkillCategoryRequestDto dto) {
        if (skillCategoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("A category with the name '" + dto.getName() + "' already exists.");
        }

        SkillCategory category = SkillCategory.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .build();

        return skillCategoryRepository.save(category);
    }

    @Override
    public SkillCategory updateCategory(Long id, SkillCategoryRequestDto dto) {
        SkillCategory existingCategory = skillCategoryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Skill category not found with id " + id));

        // If the name is changed, verify uniqueness
        if (skillCategoryRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new IllegalArgumentException("A category with the name '" + dto.getName() + "' already exists.");
        }

        existingCategory.setName(dto.getName().trim());
        existingCategory.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);

        return skillCategoryRepository.save(existingCategory);
    }

    @Override
    public Page<SkillCategory> getAllCategories(int page, int size, String searchQuery) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return skillCategoryRepository.findByNameContainingIgnoreCase(searchQuery.trim(), pageable);
        }
        return skillCategoryRepository.findAll(pageable);
    }

    @Override
    public List<SkillCategory> getAllCategories() {
        return skillCategoryRepository.findAll();
    }

    @Override
    public SkillCategory getCategoryById(Long id) {
        return skillCategoryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Skill category not found with id " + id));
    }
}
