package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.chat.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.dto.chat.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.dto.chat.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.RoleType;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.ChatService;
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
@DisplayName("ChatController Integration Tests")
class ChatControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ChatService chatService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatController chatController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing ChatControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed ChatControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
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
     * Purpose: Verifies listing user conversations.
     * Preconditions: Valid Authorization token provided.
     * Expected Behavior: Returns HTTP 200 OK with list of conversation summaries.
     */
    @Test
    @DisplayName("Should return 200 OK with conversation summaries")
    void whenGetConversations_thenReturns200AndConversations() throws Exception {
        mockUserAuth();
        ConversationSummaryDto dto = ConversationSummaryDto.builder()
                .connectionRequestId(10L)
                .partnerId(2L)
                .partnerName("Bob")
                .lastMessagePreview("Hello!")
                .build();

        given(chatService.getConversations(1L)).willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/chat/conversations")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].partnerName").value("Bob"));
    }

    /**
     * Purpose: Verifies retrieving chat messages for a specific conversation.
     * Preconditions: Valid connection request ID and Authorization token.
     * Expected Behavior: Returns HTTP 200 OK with message history.
     */
    @Test
    @DisplayName("Should return 200 OK with messages for conversation")
    void whenGetMessages_thenReturns200AndMessageList() throws Exception {
        mockUserAuth();
        ChatMessageDto msg = ChatMessageDto.builder()
                .id(100L)
                .content("Hi Alice")
                .build();

        given(chatService.getMessages(10L, 1L)).willReturn(List.of(msg));

        mockMvc.perform(get("/api/v1/chat/10/messages")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].content").value("Hi Alice"));
    }

    /**
     * Purpose: Verifies sending a new direct message in a conversation.
     * Preconditions: Valid SendMessageRequestDto payload provided.
     * Expected Behavior: Returns HTTP 200 OK with saved ChatMessageDto.
     */
    @Test
    @DisplayName("Should return 200 OK on sending new chat message")
    void givenValidMessage_whenSendMessage_thenReturns201AndMessage() throws Exception {
        mockUserAuth();
        SendMessageRequestDto request = new SendMessageRequestDto();
        request.setContent("Let's talk tomorrow!");

        ChatMessageDto response = ChatMessageDto.builder()
                .id(101L)
                .content("Let's talk tomorrow!")
                .build();

        given(chatService.sendNewMessage(eq(10L), eq(1L), any(SendMessageRequestDto.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/chat/10/send")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Let's talk tomorrow!"));
    }
}
