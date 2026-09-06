package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.admin.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.repository.SkillCategoryRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.LearningGoalRepository;
import com.crimsonlogic.open_petal_backend.repository.UserSkillRepository;
import com.crimsonlogic.open_petal_backend.service.SkillService;
import com.crimsonlogic.open_petal_backend.dto.skill.SkillDetailResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final LearningGoalRepository learningGoalRepository;
    private final UserSkillRepository userSkillRepository;

    @Override
    @Transactional
    public ApiResponse.SkillDTO createSkill(ApiResponse.SkillDTO skillDTO) {
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
    public ApiResponse.SkillDTO addSkillToCategory(AddSkillRequestDto requestDto) {
        SkillCategory category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDto.getCategoryId()));

        Skill skill = Skill.builder()
                .category(category)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .isActive(true)
                .build();

        Skill savedSkill = skillRepository.save(skill);
        return mapToDTO(savedSkill);
    }

    @Override
    @Transactional
    public ApiResponse.SkillDTO updateSkill(Long id, ApiResponse.SkillDTO skillDTO) {
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
    public ApiResponse.SkillDTO getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + id));
        return mapToDTO(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiResponse.SkillDTO> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ApiResponse.SkillDTO> getSkillsByCategory(Long categoryId, int page, int size, String searchQuery) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Skill> skillPage;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            skillPage = skillRepository.findByCategoryIdAndNameContainingIgnoreCaseAndIsDeletedFalse(categoryId, searchQuery.trim(), pageable);
        } else {
            skillPage = skillRepository.findByCategoryIdAndIsDeletedFalse(categoryId, pageable);
        }
        return skillPage.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillDetailResponseDto getAdminSkillDetails(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + skillId));
                
        long learnersCount = learningGoalRepository.countBySkillIdAndIsDeletedFalseAndUserLoginIsDeletedFalse(skillId);
        long teachersCount = userSkillRepository.countBySkillIdAndIsDeletedFalseAndUserLoginIsDeletedFalse(skillId);
        
        return SkillDetailResponseDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .isActive(skill.getIsActive())
                .categoryId(skill.getCategory() != null ? skill.getCategory().getId() : null)
                .categoryName(skill.getCategory() != null ? skill.getCategory().getName() : null)
                .createdAt(skill.getCreatedAt())
                .learnersCount(learnersCount)
                .teachersCount(teachersCount)
                .build();
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }

    private ApiResponse.SkillDTO mapToDTO(Skill skill) {
        return ApiResponse.SkillDTO.builder()
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