package com.crimsonlogic.open_petal_backend.dto.admin;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AddSkillRequestDto Validation Tests")
class AddSkillRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Purpose: Ensures a correctly populated DTO passes validation.
     * Preconditions: All fields populated with valid data.
     * Expected Behavior: 0 constraint violations.
     */
    @Test
    @DisplayName("Should pass validation with valid data")
    void givenValidDto_whenValidated_thenNoViolations() {
        // Arrange
        AddSkillRequestDto dto = new AddSkillRequestDto(1L, "Java", "Programming Language");

        // Act
        Set<ConstraintViolation<AddSkillRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    /**
     * Purpose: Prevents adding skills without a category link.
     * Preconditions: categoryId is null.
     * Expected Behavior: 1 violation on categoryId.
     */
    @Test
    @DisplayName("Should fail validation when categoryId is null")
    void givenNullCategoryId_whenValidated_thenViolation() {
        // Arrange
        AddSkillRequestDto dto = new AddSkillRequestDto(null, "Java", "Programming");

        // Act
        Set<ConstraintViolation<AddSkillRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Category ID is required");
    }

    /**
     * Purpose: Prevents skills with empty names.
     * Preconditions: name is blank.
     * Expected Behavior: violations for NotBlank and Size constraints.
     */
    @Test
    @DisplayName("Should fail validation when name is blank")
    void givenBlankName_whenValidated_thenViolation() {
        // Arrange
        AddSkillRequestDto dto = new AddSkillRequestDto(1L, "   ", "Desc");

        // Act
        Set<ConstraintViolation<AddSkillRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    /**
     * Purpose: Enforces minimum length constraint on skill names.
     * Preconditions: name is 1 character long.
     * Expected Behavior: 1 violation on name size.
     */
    @Test
    @DisplayName("Should fail validation when name is too short")
    void givenNameTooShort_whenValidated_thenViolation() {
        // Arrange
        AddSkillRequestDto dto = new AddSkillRequestDto(1L, "A", "Desc");

        // Act
        Set<ConstraintViolation<AddSkillRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("between 2 and 100");
    }

    /**
     * Purpose: Guards against DB overflow for description text.
     * Preconditions: description is > 500 characters.
     * Expected Behavior: 1 violation on description size.
     */
    @Test
    @DisplayName("Should fail validation when description exceeds max length")
    void givenDescriptionTooLong_whenValidated_thenViolation() {
        // Arrange
        String longDesc = "a".repeat(501);
        AddSkillRequestDto dto = new AddSkillRequestDto(1L, "Java", longDesc);

        // Act
        Set<ConstraintViolation<AddSkillRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not exceed 500 characters");
    }
}
