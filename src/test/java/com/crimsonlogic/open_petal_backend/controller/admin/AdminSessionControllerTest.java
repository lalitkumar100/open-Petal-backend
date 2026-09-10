package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminCancelSessionRequestDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.SessionService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminSessionController Integration Tests")
class AdminSessionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SessionService sessionService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AdminSessionController adminSessionController;

    private static final String AUTH_HEADER = "Bearer valid.admin.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing AdminSessionControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed AdminSessionControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminSessionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private void mockAdminAuth() {
        given(authService.validateToken("valid.admin.token")).willReturn(true);
        given(authService.getRoleFromToken("valid.admin.token")).willReturn("ROLE_ADMIN");
    }

    /**
     * Purpose: Verifies listing all global learning sessions across the platform.
     * Preconditions: Admin is authenticated.
     * Expected Behavior: Returns HTTP 200 OK with list of session summaries.
     */
    @Test
    @DisplayName("Should return 200 OK with global sessions list")
    void whenGetAllGlobalSessions_thenReturns200AndSessionsList() throws Exception {
        mockAdminAuth();
        AdminSessionSummaryResponseDto summary = AdminSessionSummaryResponseDto.builder()
                .id(100L)
                .learnerName("Alice")
                .mentorName("Bob")
                .status(SessionStatus.SCHEDULED)
                .build();

        given(sessionService.getAllGlobalSessions()).willReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/admin/sessions")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(100))
                .andExpect(jsonPath("$.data[0].learnerName").value("Alice"));
    }

    /**
     * Purpose: Verifies fetching admin details for a specific session.
     * Preconditions: Session ID exists.
     * Expected Behavior: Returns HTTP 200 OK with detailed admin session DTO.
     */
    @Test
    @DisplayName("Should return 200 OK with admin session details for valid ID")
    void givenValidSessionId_whenGetAdminSessionDetails_thenReturns200AndDetails() throws Exception {
        mockAdminAuth();
        AdminSessionDetailResponseDto details = AdminSessionDetailResponseDto.adminDetailBuilder()
                .id(100L)
                .learnerName("Alice")
                .mentorName("Bob")
                .status(SessionStatus.SCHEDULED)
                .build();

        given(sessionService.getAdminSessionDetails(100L)).willReturn(details);

        mockMvc.perform(get("/api/v1/admin/sessions/100")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    /**
     * Purpose: Tests administrative cancellation of an active or pending session with reason.
     * Preconditions: Session ID exists and cancellation reason is provided.
     * Expected Behavior: Returns HTTP 200 OK and cancellation success message.
     */
    @Test
    @DisplayName("Should return 200 OK on admin session cancellation")
    void givenValidCancellation_whenAdminCancelSession_thenReturns200Success() throws Exception {
        mockAdminAuth();
        AdminCancelSessionRequestDto request = new AdminCancelSessionRequestDto();
        request.setReason("Violation of platform policy");

        doNothing().when(sessionService).adminCancelSession(eq(100L), eq("Violation of platform policy"));

        mockMvc.perform(put("/api/v1/admin/sessions/100/cancel")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Session force-cancelled successfully"));
    }
}
