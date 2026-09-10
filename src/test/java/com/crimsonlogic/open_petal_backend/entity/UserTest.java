package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity Tests")
class UserTest {

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


    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .credit(100)
                .build();
    }

    /**
     * Purpose: Verifies that a user born more than 18 years ago is considered valid age.
     * Preconditions: User DOB is set to 20 years ago.
     * Expected Behavior: isOfValidAge() returns true.
     */
    @Test
    @DisplayName("Should return true when user is 18 or older")
    void givenValidDob_whenIsOfValidAge_thenReturnsTrue() {
        // Arrange
        user.setDob(LocalDate.now().minusYears(20));

        // Act
        boolean isValidAge = user.isOfValidAge();

        // Assert
        assertThat(isValidAge)
                .as("User should be considered valid age if they are 20 years old")
                .isTrue();
    }

    /**
     * Purpose: Rejects underage users.
     * Preconditions: User DOB is set to 17 years ago.
     * Expected Behavior: isOfValidAge() returns false.
     */
    @Test
    @DisplayName("Should return false when user is under 18")
    void givenInvalidDob_whenIsOfValidAge_thenReturnsFalse() {
        // Arrange
        user.setDob(LocalDate.now().minusYears(17));

        // Act
        boolean isValidAge = user.isOfValidAge();

        // Assert
        assertThat(isValidAge)
                .as("User should be rejected if they are 17 years old")
                .isFalse();
    }

    /**
     * Purpose: Handles null DOB gracefully.
     * Preconditions: User DOB is null.
     * Expected Behavior: isOfValidAge() returns false without throwing NPE.
     */
    @Test
    @DisplayName("Should return false when DOB is null")
    void givenNullDob_whenIsOfValidAge_thenReturnsFalse() {
        // Arrange
        user.setDob(null);

        // Act
        boolean isValidAge = user.isOfValidAge();

        // Assert
        assertThat(isValidAge)
                .as("Null DOB should result in false for age validation")
                .isFalse();
    }

    /**
     * Purpose: Validates full name formatting.
     * Preconditions: Both firstName and lastName are populated.
     * Expected Behavior: getFullName() returns space-separated concatenation.
     */
    @Test
    @DisplayName("Should return concatenated full name")
    void givenFirstAndLastName_whenGetFullName_thenReturnsConcatenated() {
        // Arrange is done in setup
        
        // Act
        String fullName = user.getFullName();

        // Assert
        assertThat(fullName)
                .as("Full name should correctly concatenate first and last name")
                .isEqualTo("John Doe");
    }

    /**
     * Purpose: Handles missing last name.
     * Preconditions: lastName is null.
     * Expected Behavior: getFullName() returns only the first name.
     */
    @Test
    @DisplayName("Should return only first name when last name is missing")
    void givenOnlyFirstName_whenGetFullName_thenReturnsFirstName() {
        // Arrange
        user.setLastName(null);

        // Act
        String fullName = user.getFullName();

        // Assert
        assertThat(fullName)
                .as("Should just return first name if last name is missing")
                .isEqualTo("John");
    }
}
