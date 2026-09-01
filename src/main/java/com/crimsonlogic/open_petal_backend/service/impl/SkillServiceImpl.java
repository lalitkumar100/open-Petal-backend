package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.SkillDTO;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.repository.SkillCategoryRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.service.SkillService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillCategoryRepository categoryRepository;

    @Override
    @Transactional
    public SkillDTO createSkill(SkillDTO skillDTO) {
        SkillCategory category = categoryRepository.findById(skillDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + skillDTO.getCategoryId()));

        Skill skill = Skill.builder()
                .category(category)
                .name(skillDTO.getName())
                .description(skillDTO.getDescription())
                .isActive(skillDTO.getIsActive() != null ? skillDTO.getIsActive() : true)
                .build();

        Skill savedSkill = skillRepository.save(skill);
        return mapToDTO(savedSkill);
    }

    @Override
    @Transactional
    public SkillDTO updateSkill(Long id, SkillDTO skillDTO) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + id));

        if (skillDTO.getCategoryId() != null) {
            SkillCategory category = categoryRepository.findById(skillDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + skillDTO.getCategoryId()));
            skill.setCategory(category);
        }

        skill.setName(skillDTO.getName());
        skill.setDescription(skillDTO.getDescription());
        if (skillDTO.getIsActive() != null) {
            skill.setIsActive(skillDTO.getIsActive());
        }

        Skill updatedSkill = skillRepository.save(skill);
        return mapToDTO(updatedSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillDTO getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + id));
        return mapToDTO(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }

    private SkillDTO mapToDTO(Skill skill) {
        return SkillDTO.builder()
                .id(skill.getId())
                .categoryId(skill.getCategory() != null ? skill.getCategory().getId() : null)
                .categoryName(skill.getCategory() != null ? skill.getCategory().getName() : null)
                .name(skill.getName())
                .slug(skill.getSlug())
                .description(skill.getDescription())
                .isActive(skill.getIsActive())
                .build();
    }
}