package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.ResolveConflictRequestDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import com.crimsonlogic.open_petal_backend.service.AdminConflictService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminConflictController Integration Tests")
class AdminConflictControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AdminConflictService adminConflictService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminConflictController adminConflictController;

    private static final String AUTH_HEADER = "Bearer valid.admin.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing AdminConflictControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed AdminConflictControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminConflictController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private void mockAdminAuth() {
        given(authService.validateToken("valid.admin.token")).willReturn(true);
        given(authService.getRoleFromToken("valid.admin.token")).willReturn("ROLE_ADMIN");
    }

    /**
     * Purpose: Verifies listing all conflicts for admin review.
     * Preconditions: Conflicts exist in service repository.
     * Expected Behavior: Returns HTTP 200 OK with list of conflict summaries.
     */
    @Test
    @DisplayName("Should return 200 OK with conflicts list")
    void whenGetAllConflicts_thenReturns200AndConflictList() throws Exception {
        mockAdminAuth();
        AdminConflictSummaryResponseDto summary = AdminConflictSummaryResponseDto.builder()
                .conflictId(1L)
                .sessionId(10L)
                .learnerName("Alice")
                .mentorName("Bob")
                .status(ConflictStatus.OPEN)
                .build();

        given(adminConflictService.getAllConflictsForAdmin()).willReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/admin/conflicts")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].conflictId").value(1))
                .andExpect(jsonPath("$.data[0].learnerName").value("Alice"));
    }

    /**
     * Purpose: Verifies fetching full details for a specific conflict ID.
     * Preconditions: Conflict ID exists.
     * Expected Behavior: Returns HTTP 200 OK with admin conflict details.
     */
    @Test
    @DisplayName("Should return 200 OK with conflict details for valid ID")
    void givenValidConflictId_whenGetConflictDetails_thenReturns200AndDetails() throws Exception {
        mockAdminAuth();
        AdminConflictDetailResponseDto details = AdminConflictDetailResponseDto.builder()
                .conflictId(1L)
                .sessionId(10L)
                .learnerStory("Learner story")
                .mentorStory("Mentor story")
                .status(ConflictStatus.OPEN)
                .build();

        given(adminConflictService.getAdminConflictDetails(1L)).willReturn(details);

        mockMvc.perform(get("/api/v1/admin/conflicts/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conflictId").value(1))
                .andExpect(jsonPath("$.data.learnerStory").value("Learner story"));
    }

    /**
     * Purpose: Tests resolving a conflict with a specific decision.
     * Preconditions: Valid ResolveConflictRequestDto provided.
     * Expected Behavior: Returns HTTP 200 OK and resolution success message.
     */
    @Test
    @DisplayName("Should return 200 OK on successful conflict resolution")
    void givenValidResolutionRequest_whenResolveConflict_thenReturns200Success() throws Exception {
        mockAdminAuth();
        given(authService.getEmailFromToken("valid.admin.token")).willReturn("admin@openpetal.com");
        User adminUser = new User();
        adminUser.setId(99L);
        given(userService.getUserByEmail("admin@openpetal.com")).willReturn(Optional.of(adminUser));

        ResolveConflictRequestDto request = new ResolveConflictRequestDto();
        request.setResolutionAction("REFUND_LEARNER");
        request.setAdminNotes("Refunding credits to learner");

        doNothing().when(adminConflictService).resolveConflict(eq(1L), eq(99L), any(ResolveConflictRequestDto.class));

        mockMvc.perform(put("/api/v1/admin/conflicts/1/resolve")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Conflict resolved successfully"));
    }
}
