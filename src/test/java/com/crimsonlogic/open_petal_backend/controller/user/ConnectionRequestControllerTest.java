package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.connection.ConnectionRequestResponseDto;
import com.crimsonlogic.open_petal_backend.dto.connection.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConnectionStatus;
import com.crimsonlogic.open_petal_backend.enums.RoleType;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.ConnectionRequestService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectionRequestController Integration Tests")
class ConnectionRequestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ConnectionRequestService connectionRequestService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ConnectionRequestController connectionRequestController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing ConnectionRequestControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed ConnectionRequestControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(connectionRequestController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private void mockUserAuth() {
        given(authService.validateToken("valid.user.token")).willReturn(true);
        given(authService.getRoleFromToken("valid.user.token")).willReturn("ROLE_USER");
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
     * Purpose: Verifies sending a peer connection request.
     * Preconditions: Valid SendConnectionRequestDto payload provided.
     * Expected Behavior: Returns HTTP 200 OK with ConnectionRequest entity ID.
     */
    @Test
    @DisplayName("Should return 200 OK on sending connection request")
    void givenValidRequest_whenSendConnectionRequest_thenReturns201() throws Exception {
        mockUserAuth();
        SendConnectionRequestDto dto = new SendConnectionRequestDto();
        dto.setReceiverId(2L);
        dto.setPrimarySkillId(10L);
        dto.setInitialMessage("Hi!");

        ConnectionRequest requestEntity = ConnectionRequest.builder()
                .id(50L)
                .status(ConnectionStatus.PENDING)
                .build();

        given(connectionRequestService.sendConnectionRequest(eq(1L), any(SendConnectionRequestDto.class))).willReturn(requestEntity);

        mockMvc.perform(post("/api/v1/connections/send")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(50));
    }

    /**
     * Purpose: Verifies accepting a pending connection request.
     * Preconditions: Request ID exists and user is receiver.
     * Expected Behavior: Returns HTTP 200 OK with ACCEPTED ConnectionRequest entity ID.
     */
    @Test
    @DisplayName("Should return 200 OK on accepting connection request")
    void givenRequestId_whenAcceptConnectionRequest_thenReturns200Accepted() throws Exception {
        mockUserAuth();
        ConnectionRequest acceptedEntity = ConnectionRequest.builder()
                .id(50L)
                .status(ConnectionStatus.ACCEPTED)
                .build();

        given(connectionRequestService.acceptConnectionRequest(1L, 50L)).willReturn(acceptedEntity);

        mockMvc.perform(post("/api/v1/connections/accept/50")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(50));
    }

    /**
     * Purpose: Verifies listing sent connection requests for a user.
     * Preconditions: Valid Authorization token provided.
     * Expected Behavior: Returns HTTP 200 OK with list of sent requests.
     */
    @Test
    @DisplayName("Should return 200 OK with list of sent connection requests")
    void whenGetSentConnectionRequests_thenReturns200AndList() throws Exception {
        mockUserAuth();
        ConnectionRequestResponseDto sentDto = ConnectionRequestResponseDto.builder()
                .id(50L)
                .receiverName("Bob")
                .status(ConnectionStatus.PENDING)
                .build();

        given(connectionRequestService.getSentConnectionRequests(1L)).willReturn(List.of(sentDto));

        mockMvc.perform(get("/api/v1/connections/sent")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].receiverName").value("Bob"));
    }
}
