package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.auth.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.repository.LoginRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Mockito Tests")
class AuthServiceImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    /**
     * Purpose: Verifies full registration transaction flow.
     * Preconditions: Email does not exist.
     * Expected Behavior: Saves Login, saves User, returns DTO.
     */
    @Test
    @DisplayName("Should successfully register a new user")
    void givenValidRegistration_whenRegister_thenCreatesUserAndReturnsDto() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("new@example.com");
        request.setPassword("Password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDob(LocalDate.now().minusYears(30));

        Login savedLogin = new Login();
        savedLogin.setId(1L);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("new@example.com");

        given(loginRepository.existsByEmail(anyString())).willReturn(false);
        given(loginRepository.save(any(Login.class))).willReturn(savedLogin);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // Act
        RegisterResponseDto response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        
        verify(loginRepository, times(1)).save(any(Login.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Purpose: Prevents duplicate user accounts via conflict exception.
     * Preconditions: Email exists in LoginRepository.
     * Expected Behavior: Throws CustomException(CONFLICT).
     */
    @Test
    @DisplayName("Should throw CustomException when email is already registered")
    void givenExistingEmail_whenRegister_thenThrowsCustomException() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("existing@example.com");

        given(loginRepository.existsByEmail("existing@example.com")).willReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("Email is already registered")
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);

        verify(loginRepository, times(0)).save(any(Login.class));
    }
}
