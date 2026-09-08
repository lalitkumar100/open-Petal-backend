package com.crimsonlogic.open_petal_backend.dto.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegisterRequestDto Validation Tests")
class RegisterRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Purpose: Ensures valid registrations are accepted.
     * Preconditions: Fully valid registration DTO.
     * Expected Behavior: 0 violations.
     */
    @Test
    @DisplayName("Should pass validation with valid registration data")
    void givenValidRegistration_whenValidated_thenNoViolations() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDob(LocalDate.now().minusYears(30));

        // Act
        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    /**
     * Purpose: Enforces strict password length requirements.
     * Preconditions: Password less than 8 characters.
     * Expected Behavior: 1 violation on password length.
     */
    @Test
    @DisplayName("Should fail validation on short password")
    void givenPasswordTooShort_whenValidated_thenViolation() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("valid@example.com");
        dto.setPassword("short");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDob(LocalDate.now().minusYears(30));

        // Act
        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("at least 8 characters");
    }

    /**
     * Purpose: Enforces Regex pattern for names.
     * Preconditions: First name containing numbers.
     * Expected Behavior: 1 violation on regex pattern.
     */
    @Test
    @DisplayName("Should fail validation when name contains numbers")
    void givenNumbersInName_whenValidated_thenViolation() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123");
        dto.setFirstName("John123");
        dto.setLastName("Doe");
        dto.setDob(LocalDate.now().minusYears(30));

        // Act
        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("First name must contain only letters");
    }

    /**
     * Purpose: Blocks users under 28 from registering via custom @AssertTrue.
     * Preconditions: User is 20 years old.
     * Expected Behavior: 1 violation on the isOfValidAge method constraint.
     */
    @Test
    @DisplayName("Should fail validation when user is under 28")
    void givenUnderageUser_whenValidated_thenViolation() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDob(LocalDate.now().minusYears(20));

        // Act
        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("You must be at least 28 years old");
    }
    
    /**
     * Purpose: Validates presence of Date of Birth.
     * Preconditions: DOB is null.
     * Expected Behavior: 1 violation on DOB.
     */
    @Test
    @DisplayName("Should fail validation when DOB is null")
    void givenNullDob_whenValidated_thenViolation() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDob(null);

        // Act
        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(dto);

        // Assert
        // Might be multiple violations due to @NotNull and @AssertTrue reacting to null
        assertThat(violations).isNotEmpty();
    }
}
