package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.enums.RoleType;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.service.UserSkillService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserSkillService userSkillService;

    @Mock
    private LearningGoalService learningGoalService;

    @InjectMocks
    private UserController userController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing UserControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed UserControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private void mockUserAuth() {
        given(authService.validateToken("valid.user.token")).willReturn(true);
        given(authService.getRoleFromToken("valid.user.token")).willReturn("ROLE_USER");
        given(authService.getEmailFromToken("valid.user.token")).willReturn("user@openpetal.com");
    }

    /**
     * Purpose: Verifies fetching profile for user.
     * Preconditions: Authorization header with valid user email.
     * Expected Behavior: Returns HTTP 200 OK with user profile details.
     */
    @Test
    @DisplayName("Should return 200 OK on user profile retrieval")
    void whenGetProfile_thenReturns200AndProfile() throws Exception {
        mockUserAuth();
        Login login = new Login();
        login.setId(1L);
        login.setStatus(AccountStatus.ACTIVE);
        login.setRole(RoleType.ROLE_USER);

        User user = User.builder()
                .id(1L)
                .email("user@openpetal.com")
                .firstName("Alice")
                .lastName("Smith")
                .login(login)
                .build();

        given(userService.getUserByEmail("user@openpetal.com")).willReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/user/profile")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Alice"));
    }

    /**
     * Purpose: Verifies retrieving user skills list.
     * Preconditions: User is authenticated.
     * Expected Behavior: Returns HTTP 200 OK with list of skills.
     */
    @Test
    @DisplayName("Should return 200 OK on fetching user skills list")
    void whenGetMySkills_thenReturns200() throws Exception {
        mockUserAuth();
        User user = new User();
        user.setId(1L);

        given(userService.getUserByEmail("user@openpetal.com")).willReturn(Optional.of(user));
        given(userSkillService.getAllSkillsByUser(1L)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/user/skills")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * Purpose: Verifies fetching user learning goals.
     * Preconditions: User is authenticated.
     * Expected Behavior: Returns HTTP 200 OK with learning goals list.
     */
    @Test
    @DisplayName("Should return 200 OK on fetching user learning goals")
    void whenGetMyLearningGoals_thenReturns200() throws Exception {
        mockUserAuth();
        User user = new User();
        user.setId(1L);

        given(userService.getUserByEmail("user@openpetal.com")).willReturn(Optional.of(user));
        given(learningGoalService.getLearningGoalsByUserId(1L)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/user/learning-goals")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
