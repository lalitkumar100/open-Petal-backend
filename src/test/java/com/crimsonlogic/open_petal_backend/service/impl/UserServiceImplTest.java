package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Mockito Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Purpose: Tests basic user creation in User service.
     * Preconditions: User entity provided.
     * Expected Behavior: Returns saved User.
     */
    @Test
    @DisplayName("Should successfully create a user")
    void givenValidUser_whenCreateUser_thenReturnsSavedUser() {
        // Arrange
        User user = new User();
        given(userRepository.save(user)).willReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        assertThat(result).isSameAs(user);
        verify(userRepository).save(user);
    }

    /**
     * Purpose: Tests retrieval by ID.
     * Preconditions: User exists.
     * Expected Behavior: Returns Optional of User.
     */
    @Test
    @DisplayName("Should retrieve user by ID")
    void givenValidId_whenGetUserById_thenReturnsUser() {
        // Arrange
        User user = new User();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertThat(result).isPresent().contains(user);
    }

    /**
     * Purpose: Tests missing ID handling.
     * Preconditions: User does not exist.
     * Expected Behavior: Returns empty Optional.
     */
    @Test
    @DisplayName("Should return empty when retrieving non-existent user by ID")
    void givenInvalidId_whenGetUserById_thenReturnsEmpty() {
        // Arrange
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(99L);

        // Assert
        assertThat(result).isEmpty();
    }
}
