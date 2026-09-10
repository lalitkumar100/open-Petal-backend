package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import com.crimsonlogic.open_petal_backend.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

@DisplayName("Login Entity Tests")
class LoginTest {

    /**
     * Purpose: Forces garbage collection before all tests for professional memory management.
     */
    @BeforeAll
    static void beforeAll() {
        System.gc();
    }

    /**
     * Purpose: Forces garbage collection after all tests to free up resources.
     */
    @AfterAll
    static void afterAll() {
        System.gc();
    }


    private Login login;

    @BeforeEach
    void setUp() {
        login = Login.builder()
                .email("test@example.com")
                .build();
    }

    /**
     * Purpose: Ensures password hashing utility is called and state is updated.
     * Preconditions: Valid plain text password provided.
     * Expected Behavior: Hash is updated to mocked result.
     */
    @Test
    @DisplayName("Should update password hash when setting plain password")
    void givenPlainPassword_whenHashPassword_thenUpdatesHash() {
        try (MockedStatic<PasswordUtil> utilities = mockStatic(PasswordUtil.class)) {
            // Arrange
            utilities.when(() -> PasswordUtil.generateHash(anyString())).thenReturn("hashed-secret");

            // Act
            login.hashPassword("Secret123");

            // Assert
            assertThat(login.getPasswordHash())
                    .as("Password hash must be generated and set on the entity")
                    .isEqualTo("hashed-secret");
        }
    }

    /**
     * Purpose: Validates login matching logic.
     * Preconditions: Correct plain text password provided.
     * Expected Behavior: verifyPassword returns true.
     */
    @Test
    @DisplayName("Should return true when password matches hash")
    void givenCorrectPassword_whenVerifyPassword_thenReturnsTrue() {
        try (MockedStatic<PasswordUtil> utilities = mockStatic(PasswordUtil.class)) {
            // Arrange
            login.setPasswordHash("hashed-secret");
            utilities.when(() -> PasswordUtil.generateHash("Secret123")).thenReturn("hashed-secret");

            // Act
            boolean result = login.verifyPassword("Secret123");

            // Assert
            assertThat(result).isTrue();
        }
    }

    /**
     * Purpose: Mitigates unauthorized access via bad password.
     * Preconditions: Incorrect plain text password provided.
     * Expected Behavior: verifyPassword returns false.
     */
    @Test
    @DisplayName("Should return false when password does not match hash")
    void givenWrongPassword_whenVerifyPassword_thenReturnsFalse() {
        try (MockedStatic<PasswordUtil> utilities = mockStatic(PasswordUtil.class)) {
            // Arrange
            login.setPasswordHash("hashed-secret");
            utilities.when(() -> PasswordUtil.generateHash("WrongPass")).thenReturn("wrong-hash");

            // Act
            boolean result = login.verifyPassword("WrongPass");

            // Assert
            assertThat(result).isFalse();
        }
    }

    /**
     * Purpose: Prevents NPE on authentication check.
     * Preconditions: null plain text password provided.
     * Expected Behavior: verifyPassword returns false safely.
     */
    @Test
    @DisplayName("Should return false when plain password is null")
    void givenNullPassword_whenVerifyPassword_thenReturnsFalse() {
        // Arrange
        login.setPasswordHash("hashed-secret");

        // Act
        boolean result = login.verifyPassword(null);

        // Assert
        assertThat(result)
                .as("Verification should fail safely if input is null")
                .isFalse();
    }

    /**
     * Purpose: Guards against verification before password is set.
     * Preconditions: Login entity has no stored password hash.
     * Expected Behavior: verifyPassword returns false safely.
     */
    @Test
    @DisplayName("Should return false when no hash is stored")
    void givenNoHashStored_whenVerifyPassword_thenReturnsFalse() {
        // Arrange
        login.setPasswordHash(null);

        // Act
        boolean result = login.verifyPassword("Secret123");

        // Assert
        assertThat(result)
                .as("Verification should fail if there is no hash stored in DB")
                .isFalse();
    }
}
