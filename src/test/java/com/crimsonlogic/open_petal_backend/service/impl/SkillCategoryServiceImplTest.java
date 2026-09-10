package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.SkillCategoryRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SkillCategoryServiceImpl Tests")
class SkillCategoryServiceImplTest {

    @Mock
    private SkillCategoryRepository skillCategoryRepository;

    @InjectMocks
    private SkillCategoryServiceImpl skillCategoryService;

    @Test
    @DisplayName("Should successfully create a new skill category")
    void givenValidDto_whenCreateCategory_thenReturnsCategory() {
        SkillCategoryRequestDto dto = new SkillCategoryRequestDto();
        dto.setName("Programming");
        dto.setDescription("Software development skills");

        SkillCategory saved = SkillCategory.builder().id(1L).name("Programming").description("Software development skills").build();

        given(skillCategoryRepository.existsByNameIgnoreCase("Programming")).willReturn(false);
        given(skillCategoryRepository.save(any(SkillCategory.class))).willReturn(saved);

        SkillCategory result = skillCategoryService.createCategory(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Programming");
        verify(skillCategoryRepository).save(any(SkillCategory.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when category name already exists")
    void givenExistingCategoryName_whenCreateCategory_thenThrowsIllegalArgumentException() {
        SkillCategoryRequestDto dto = new SkillCategoryRequestDto();
        dto.setName("Programming");

        given(skillCategoryRepository.existsByNameIgnoreCase("Programming")).willReturn(true);

        assertThatThrownBy(() -> skillCategoryService.createCategory(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should throw RecordNotFoundException when getting category by invalid ID")
    void givenInvalidId_whenGetCategoryById_thenThrowsRecordNotFoundException() {
        given(skillCategoryRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> skillCategoryService.getCategoryById(999L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Skill category not found");
    }
}
