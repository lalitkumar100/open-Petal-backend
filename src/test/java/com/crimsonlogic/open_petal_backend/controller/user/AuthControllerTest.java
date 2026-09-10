package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.auth.LoginRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.LoginResponseDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing AuthControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed AuthControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    /**
     * Purpose: Tests successful signup flow and payload mapping.
     * Preconditions: Valid RegisterRequestDto provided.
     * Expected Behavior: Returns 201 Created and correctly structured ApiResponse.
     */
    @Test
    @DisplayName("Should return 201 Created on valid registration")
    void givenValidRegistration_whenRegister_thenReturns201AndDto() throws Exception {
        // Arrange
        String jsonRequest = "{\"email\":\"user@example.com\",\"password\":\"SecurePass123\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"dob\":\"1996-01-01\"}";

        RegisterResponseDto response = RegisterResponseDto.builder()
                .email("user@example.com")
                .firstName("John")
                .build();

        given(authService.register(any(RegisterRequestDto.class))).willReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }

    /**
     * Purpose: Verifies @Valid hooks into Spring MVC correctly.
     * Preconditions: Invalid email format provided.
     * Expected Behavior: Returns 400 Bad Request, intercepting the call before Service layer.
     */
    @Test
    @DisplayName("Should return 400 Bad Request on invalid email during registration")
    void givenInvalidEmail_whenRegister_thenReturns400BadRequest() throws Exception {
        // Arrange
        String jsonRequest = "{\"email\":\"invalid-email\",\"password\":\"SecurePass123\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"dob\":\"1996-01-01\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * Purpose: Verifies login endpoint returns the JWT.
     * Preconditions: Valid LoginRequestDto provided.
     * Expected Behavior: Returns 200 OK and JWT token in ApiResponse.
     */
    @Test
    @DisplayName("Should return 200 OK and token on valid login")
    void givenValidCredentials_whenLogin_thenReturns200AndToken() throws Exception {
        // Arrange
        String jsonRequest = "{\"email\":\"user@example.com\",\"password\":\"SecurePass123\"}";

        LoginResponseDto response = LoginResponseDto.builder()
                .token("jwt-token-xyz")
                .build();

        given(authService.login(any(LoginRequestDto.class))).willReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token-xyz"));
    }

    /**
     * Purpose: Ensures missing login credentials do not reach Service layer.
     * Preconditions: Blank password in LoginRequestDto.
     * Expected Behavior: Returns 400 Bad Request.
     */
    @Test
    @DisplayName("Should return 400 Bad Request on empty password during login")
    void givenEmptyPassword_whenLogin_thenReturns400BadRequest() throws Exception {
        // Arrange
        String jsonRequest = "{\"email\":\"user@example.com\",\"password\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
