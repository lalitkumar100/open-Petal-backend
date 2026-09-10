package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.session.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.RoleType;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.SessionService;
import com.crimsonlogic.open_petal_backend.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController Integration Tests")
class SessionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SessionService sessionService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionController sessionController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing SessionControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed SessionControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
    }

    private void mockUserAuth() {
        given(authService.validateToken("valid.user.token")).willReturn(true);
        given(authService.getEmailFromToken("valid.user.token")).willReturn("user@openpetal.com");

        Login login = new Login();
        login.setRole(RoleType.ROLE_USER);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@openpetal.com");
        user.setLogin(login);

        given(userService.getUserByEmail("user@openpetal.com")).willReturn(Optional.of(user));
    }

    /**
     * Purpose: Verifies booking/requesting a new learning session.
     * Preconditions: Valid CreateSessionDto payload provided.
     * Expected Behavior: Returns HTTP 200 OK with session ID.
     */
    @Test
    @DisplayName("Should return 200 OK on valid session request creation")
    void givenValidSessionRequest_whenCreateSession_thenReturns200() throws Exception {
        mockUserAuth();
        LearningSession sessionEntity = LearningSession.builder()
                .id(100L)
                .status(SessionStatus.REQUESTED)
                .build();

        given(sessionService.createSession(eq(1L), any(CreateSessionDto.class))).willReturn(sessionEntity);

        String jsonPayload = """
                {
                  "mentorId": 2,
                  "primarySkillId": 10,
                  "sessionType": "CREDIT",
                  "mode": "ONLINE",
                  "sessionDate": "2030-10-10",
                  "startTime": "10:00:00",
                  "endTime": "11:00:00",
                  "creditsHeld": 1
                }
                """;

        mockMvc.perform(post("/api/v1/sessions/create")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(100));
    }

    /**
     * Purpose: Verifies listing all active and past sessions for a user.
     * Preconditions: Valid user session query.
     * Expected Behavior: Returns HTTP 200 OK with list of session summaries.
     */
    @Test
    @DisplayName("Should return 200 OK with user's sessions list")
    void whenGetMySessions_thenReturns200AndSessionsList() throws Exception {
        mockUserAuth();
        SessionSummaryResponseDto summary = SessionSummaryResponseDto.builder()
                .id(100L)
                .partnerName("Bob")
                .status(SessionStatus.SCHEDULED)
                .build();

        given(sessionService.getMySessions(1L)).willReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/sessions/my-sessions")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].partnerName").value("Bob"));
    }

    /**
     * Purpose: Verifies fetching session details by session ID.
     * Preconditions: Session ID exists and user is participant.
     * Expected Behavior: Returns HTTP 200 OK with SessionDetailResponseDto.
     */
    @Test
    @DisplayName("Should return 200 OK with session details for valid ID")
    void givenSessionId_whenGetSessionDetails_thenReturns200AndDetails() throws Exception {
        mockUserAuth();
        SessionDetailResponseDto details = SessionDetailResponseDto.detailBuilder()
                .id(100L)
                .status(SessionStatus.SCHEDULED)
                .build();

        given(sessionService.getSessionDetails(eq(100L), eq(1L))).willReturn(details);

        mockMvc.perform(get("/api/v1/sessions/100")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100));
    }
}
