package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.aichat.ChatHistoryDto;
import com.crimsonlogic.open_petal_backend.dto.aichat.ChatMenuDto;
import com.crimsonlogic.open_petal_backend.dto.aichat.ChatRequest;
import com.crimsonlogic.open_petal_backend.dto.aichat.ChatResponse;
import com.crimsonlogic.open_petal_backend.entity.AiChat;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.RoleType;
import com.crimsonlogic.open_petal_backend.repository.AiChatMessageRepository;
import com.crimsonlogic.open_petal_backend.repository.AiChatRepository;
import com.crimsonlogic.open_petal_backend.service.AiChatService;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
@DisplayName("AiChatController Integration Tests")
class AiChatControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AiChatService aiChatService;

    @Mock
    private AiChatRepository chatRepository;

    @Mock
    private AiChatMessageRepository chatMessageRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AiChatController aiChatController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing AiChatControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed AiChatControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(aiChatController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private void mockUserAuth() {
        given(authService.validateToken("valid.user.token")).willReturn(true);
        given(authService.getRoleFromToken("valid.user.token")).willReturn("ROLE_USER");
        given(authService.getEmailFromToken("valid.user.token")).willReturn("user@openpetal.com");

        Login login = new Login();
        login.setRole(RoleType.ROLE_USER);

        User user = new User();
        user.setId(1L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setLogin(login);

        given(userService.getUserByEmail("user@openpetal.com")).willReturn(Optional.of(user));
    }

    /**
     * Purpose: Tests posting a prompt to AI assistant for structured response.
     * Preconditions: Valid ChatRequest body and headers provided.
     * Expected Behavior: Returns HTTP 200 OK with ChatResponse payload.
     */
    @Test
    @DisplayName("Should return 200 OK on valid AI chat request")
    void givenValidPrompt_whenProcessChat_thenReturns200AndResponse() throws Exception {
        mockUserAuth();

        ChatRequest request = new ChatRequest();
        request.setPrompt("Help me analyze Java skills");
        request.setNewChat(true);

        ChatResponse response = new ChatResponse();
        response.setChatId("chat-123");
        response.setTitle("Java Analysis");

        given(aiChatService.processChat(eq(null), any(ChatRequest.class), any())).willReturn(response);

        mockMvc.perform(post("/api/ai/chat")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chat_id").value("chat-123"))
                .andExpect(jsonPath("$.title").value("Java Analysis"));
    }

    /**
     * Purpose: Verifies AI chat menu list retrieval.
     * Preconditions: User is authenticated.
     * Expected Behavior: Returns HTTP 200 OK with AI assistant chat menu summaries.
     */
    @Test
    @DisplayName("Should return 200 OK on AI menu options request")
    void whenGetMenu_thenReturns200AndMenuList() throws Exception {
        mockUserAuth();

        AiChat chat = new AiChat();
        chat.setId("chat-100");
        chat.setTitle("Spring Boot Discussion");

        given(chatRepository.findByUserIdOrderByLastMessageAtDesc("1")).willReturn(List.of(chat));

        mockMvc.perform(get("/api/ai/chat-menu")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("chat-100"))
                .andExpect(jsonPath("$[0].title").value("Spring Boot Discussion"));
    }

    /**
     * Purpose: Verifies user chat history listing.
     * Preconditions: Valid chat ID provided.
     * Expected Behavior: Returns HTTP 200 OK with past AI chat session history.
     */
    @Test
    @DisplayName("Should return 200 OK on AI chat history request")
    void whenGetChatHistory_thenReturns200AndHistory() throws Exception {
        mockUserAuth();

        AiChat chat = new AiChat();
        chat.setId("chat-100");
        chat.setTitle("Spring Boot Discussion");

        given(chatRepository.findByIdAndUserId("chat-100", "1")).willReturn(Optional.of(chat));
        given(chatMessageRepository.findByChatIdOrderByCreatedAtAsc("chat-100")).willReturn(List.of());

        mockMvc.perform(get("/api/ai/chat/chat-100")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("chat-100"))
                .andExpect(jsonPath("$.title").value("Spring Boot Discussion"));
    }
}
