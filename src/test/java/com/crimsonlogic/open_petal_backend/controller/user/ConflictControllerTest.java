package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.conflict.UserConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.ConflictService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConflictController Integration Tests")
class ConflictControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ConflictService conflictService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ConflictController conflictController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing ConflictControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed ConflictControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(conflictController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private void mockUserAuth() {
        given(authService.validateToken("valid.user.token")).willReturn(true);
        given(authService.getEmailFromToken("valid.user.token")).willReturn("user@openpetal.com");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@openpetal.com");

        given(userService.getUserByEmail("user@openpetal.com")).willReturn(Optional.of(user));
    }

    /**
     * Purpose: Verifies retrieving all conflicts associated with authenticated user.
     * Preconditions: User is authenticated and has existing conflicts.
     * Expected Behavior: Returns HTTP 200 OK with list of conflict summaries.
     */
    @Test
    @DisplayName("Should return 200 OK with user conflict summary list")
    void whenGetMyConflicts_thenReturns200AndConflictList() throws Exception {
        mockUserAuth();
        UserConflictSummaryResponseDto summary = UserConflictSummaryResponseDto.builder()
                .conflictId(10L)
                .sessionId(100L)
                .partnerName("Bob")
                .status(ConflictStatus.OPEN)
                .build();

        given(conflictService.getMyConflicts(1L)).willReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/conflicts/my-conflicts")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].conflictId").value(10))
                .andExpect(jsonPath("$.data[0].partnerName").value("Bob"));
    }

    /**
     * Purpose: Verifies retrieving user conflicts when user has no active conflicts.
     * Preconditions: User is authenticated but has zero conflicts.
     * Expected Behavior: Returns HTTP 200 OK with empty array.
     */
    @Test
    @DisplayName("Should return 200 OK with empty list when user has no conflicts")
    void givenEmptyConflicts_whenGetMyConflicts_thenReturns200AndEmptyList() throws Exception {
        mockUserAuth();
        given(conflictService.getMyConflicts(1L)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/conflicts/my-conflicts")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * Purpose: Verifies missing authorization header behavior.
     * Preconditions: No Authorization header passed.
     * Expected Behavior: Throws AuthenticationException.
     */
    @Test
    @DisplayName("Should fail when Authorization header is missing")
    void givenMissingAuthHeader_whenGetMyConflicts_thenFails() {
        Assertions.assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/v1/conflicts/my-conflicts"))
        );
    }
}
