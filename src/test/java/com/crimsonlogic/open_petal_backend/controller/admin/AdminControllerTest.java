package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminDashboardDto;
import com.crimsonlogic.open_petal_backend.dto.user.UserStatusUpdateDto;
import com.crimsonlogic.open_petal_backend.service.AdminDashboardService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
import com.crimsonlogic.open_petal_backend.service.UserSkillService;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import com.crimsonlogic.open_petal_backend.service.SkillCategoryService;
import com.crimsonlogic.open_petal_backend.service.SkillService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Integration Tests")
class AdminControllerTest {

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

    @Mock
    private SkillCategoryService skillCategoryService;

    @Mock
    private SkillService skillService;

    @Mock
    private AdminDashboardService adminDashboardService;

    @InjectMocks
    private AdminController adminController;

    private static final String AUTH_HEADER = "Bearer valid.admin.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing AdminControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed AdminControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private void mockAdminAuth() {
        given(authService.validateToken("valid.admin.token")).willReturn(true);
        given(authService.getRoleFromToken("valid.admin.token")).willReturn("ROLE_ADMIN");
        given(authService.getEmailFromToken("valid.admin.token")).willReturn("admin@openpetal.com");
    }

    /**
     * Purpose: Verifies fetching high-level dashboard metrics for administrators.
     * Preconditions: Admin is authenticated.
     * Expected Behavior: Returns HTTP 200 OK with total users, sessions, and conflict metrics.
     */
    @Test
    @DisplayName("Should return 200 OK with dashboard metrics")
    void whenGetDashboardStats_thenReturns200AndStats() throws Exception {
        mockAdminAuth();
        AdminDashboardDto stats = new AdminDashboardDto();
        stats.setTotalUsers(150L);
        stats.setTotalSessions(12L);
        stats.setTotalConflicts(2L);

        given(adminDashboardService.getDashboardStats()).willReturn(stats);

        mockMvc.perform(get("/api/v1/admin/dashboard")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").value(150))
                .andExpect(jsonPath("$.data.totalSessions").value(12));
    }

    /**
     * Purpose: Verifies retrieving admin user overview page.
     * Preconditions: Admin is authenticated.
     * Expected Behavior: Returns HTTP 200 OK with user page.
     */
    @Test
    @DisplayName("Should return 200 OK on user overview request")
    void whenGetUsersOverview_thenReturns200() throws Exception {
        mockAdminAuth();
        given(userService.getPaginatedUsers(anyInt(), anyInt(), anyString(), anyString(), isNull(), isNull()))
                .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 5), 0));

        mockMvc.perform(get("/api/v1/admin/user")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Purpose: Tests blocking a user account by admin.
     * Preconditions: Valid UserStatusUpdateDto provided.
     * Expected Behavior: Returns HTTP 200 OK and status update message.
     */
    @Test
    @DisplayName("Should return 200 OK on blocking a user")
    void givenStatusUpdateRequest_whenBlockUser_thenReturns200Success() throws Exception {
        mockAdminAuth();
        UserStatusUpdateDto request = new UserStatusUpdateDto();
        request.setReason("Terms violation");

        doNothing().when(userService).blockUser(eq(10L), eq("Terms violation"));

        mockMvc.perform(patch("/api/v1/admin/users/10/block")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User blocked successfully"));
    }
}
