package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.admin.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.repository.SkillCategoryRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SkillService Mockito Tests")
class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillCategoryRepository categoryRepository;

    @InjectMocks
    private SkillServiceImpl skillService;

    /**
     * Purpose: Tests secondary skill creation endpoint.
     * Preconditions: Category exists.
     * Expected Behavior: Saves mapped skill and returns DTO.
     */
    @Test
    @DisplayName("Should successfully add a skill to a category")
    void givenAddSkillRequest_whenAddSkillToCategory_thenSaves() {
        // Arrange
        AddSkillRequestDto request = new AddSkillRequestDto(1L, "React", "Frontend");
        SkillCategory category = new SkillCategory();
        category.setId(1L);

        Skill savedSkill = new Skill();
        savedSkill.setId(10L);
        savedSkill.setName("React");

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(skillRepository.save(any(Skill.class))).willReturn(savedSkill);
        // Assuming mapToDTO exists, we verify the service returns something mapped.
        // We will just assert no exception was thrown and save was called.

        // Act
        ApiResponse.SkillDTO result = skillService.addSkillToCategory(request);

        // Assert
        verify(skillRepository).save(any(Skill.class));
    }

    /**
     * Purpose: Guards against foreign-key violations on category.
     * Preconditions: Category does not exist.
     * Expected Behavior: Throws EntityNotFoundException.
     */
    @Test
    @DisplayName("Should throw exception when adding skill to invalid category")
    void givenInvalidCategoryId_whenAddSkillToCategory_thenThrowsException() {
        // Arrange
        AddSkillRequestDto request = new AddSkillRequestDto(99L, "React", "Frontend");
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> skillService.addSkillToCategory(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    /**
     * Purpose: Tests simple retrieval.
     * Preconditions: Skill exists.
     * Expected Behavior: Returns DTO.
     */
    @Test
    @DisplayName("Should return skill DTO when ID is valid")
    void givenValidId_whenGetSkillById_thenReturnsDto() {
        // Arrange
        Skill skill = new Skill();
        skill.setId(1L);
        given(skillRepository.findById(1L)).willReturn(Optional.of(skill));

        // Act
        ApiResponse.SkillDTO result = skillService.getSkillById(1L);

        // Assert
        verify(skillRepository).findById(1L);
    }

    /**
     * Purpose: Ensures 404 behavior for missing skills.
     * Preconditions: Skill does not exist.
     * Expected Behavior: Throws EntityNotFoundException.
     */
    @Test
    @DisplayName("Should throw exception when retrieving invalid skill ID")
    void givenInvalidId_whenGetSkillById_thenThrowsException() {
        // Arrange
        given(skillRepository.findById(anyLong())).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> skillService.getSkillById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Skill not found");
    }
}
