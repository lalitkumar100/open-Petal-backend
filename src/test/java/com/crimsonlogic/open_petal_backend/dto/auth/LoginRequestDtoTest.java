package com.crimsonlogic.open_petal_backend.dto.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginRequestDto Tests")
class LoginRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Purpose: Verifies standard login payload acceptance.
     * Preconditions: Valid email and password.
     * Expected Behavior: 0 constraint violations.
     */
    @Test
    @DisplayName("Should pass validation with valid credentials")
    void givenValidCredentials_whenValidated_thenNoViolations() {
        // Arrange
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("SecurePass123");

        // Act
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isEmpty();
    }

    /**
     * Purpose: Blocks malformed emails at the controller boundary.
     * Preconditions: Invalid email string.
     * Expected Behavior: 1 violation on email format.
     */
    @Test
    @DisplayName("Should fail validation on invalid email format")
    void givenInvalidEmailFormat_whenValidated_thenViolation() {
        // Arrange
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("invalid-email");
        dto.setPassword("SecurePass123");

        // Act
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Please provide a valid email address");
    }

    /**
     * Purpose: Rejects missing email payloads.
     * Preconditions: Blank email.
     * Expected Behavior: violation on blank email.
     */
    @Test
    @DisplayName("Should fail validation when email is blank")
    void givenBlankEmail_whenValidated_thenViolation() {
        // Arrange
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("");
        dto.setPassword("SecurePass123");

        // Act
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
    }
    
    /**
     * Purpose: Rejects missing password payloads.
     * Preconditions: Blank password.
     * Expected Behavior: violation on blank password.
     */
    @Test
    @DisplayName("Should fail validation when password is blank")
    void givenBlankPassword_whenValidated_thenViolation() {
        // Arrange
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@test.com");
        dto.setPassword("   ");

        // Act
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    /**
     * Purpose: Tests Jackson mapping from JSON payload.
     * Preconditions: Valid JSON string matching LoginRequestDto.
     * Expected Behavior: Object deserializes cleanly.
     */
    @Test
    @DisplayName("Should correctly deserialize from JSON")
    void givenJson_whenDeserialized_thenMapsFieldsCorrectly() throws Exception {
        // Arrange
        String json = "{\"email\":\"user@example.com\",\"password\":\"mypassword\"}";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        LoginRequestDto dto = mapper.readValue(json, LoginRequestDto.class);

        // Assert
        assertThat(dto.getEmail()).isEqualTo("user@example.com");
        assertThat(dto.getPassword()).isEqualTo("mypassword");
    }
}
