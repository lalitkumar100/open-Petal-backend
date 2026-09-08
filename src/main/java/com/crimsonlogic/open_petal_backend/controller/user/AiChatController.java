package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.aichat.*;
import com.crimsonlogic.open_petal_backend.entity.AiChat;
import com.crimsonlogic.open_petal_backend.repository.AiChatMessageRepository;
import com.crimsonlogic.open_petal_backend.repository.AiChatRepository;
import com.crimsonlogic.open_petal_backend.service.AiChatService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for interacting with the AI Agent.
 * Provides endpoints for sending chat prompts, retrieving history, and viewing past sessions.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService chatService;
    private final AiChatRepository chatRepository;
    private final AiChatMessageRepository chatMessageRepository;
    private final AuthService authService;
    private final UserService userService;

    /**
     * Submits a prompt to the AI agent and returns its response.
     *
     * @param authHeader the authorization header containing the user token
     * @param id optional ID of an existing chat session to continue
     * @param request the payload containing the user's prompt
     * @return ResponseEntity containing the ChatResponse from the AI
     */
    @PostMapping({"/chat", "/chat/{id}"})
    public ResponseEntity<ChatResponse> processPipeline(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable(required = false) String id,
            @RequestBody ChatRequest request) {
            
        UserContext user = authenticateAndGetUserContext(authHeader);
        
        ChatResponse response = chatService.processChat(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a summary list of all past AI chat sessions for the user.
     *
     * @param authHeader the authorization header containing the user token
     * @return ResponseEntity containing a list of ChatMenuDto
     */
    @GetMapping("/chat-menu")
    public ResponseEntity<List<ChatMenuDto>> getMenu(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        UserContext user = authenticateAndGetUserContext(authHeader);
        
        List<AiChat> chats = chatRepository.findByUserIdOrderByLastMessageAtDesc(user.getId());
        List<ChatMenuDto> mappedList = chats.stream()
            .map(c -> new ChatMenuDto(
                c.getId(), 
                c.getTitle(), 
                c.getLastMessageAt() != null ? c.getLastMessageAt().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""
            ))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(mappedList);
    }

    /**
     * Retrieves the full message history of a specific AI chat session.
     *
     * @param authHeader the authorization header containing the user token
     * @param id the unique identifier of the chat session
     * @return ResponseEntity containing the ChatHistoryDto
     */
    @GetMapping("/chat/{id}")
    public ResponseEntity<ChatHistoryDto> getHistory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String id) {
        
        UserContext user = authenticateAndGetUserContext(authHeader);
        
        AiChat chat = chatRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
            
        List<Object> messages = chatMessageRepository.findByChatIdOrderByCreatedAtAsc(id)
            .stream()
            .map(m -> m.getContent())
            .collect(Collectors.toList());
            
        ChatHistoryDto historyDto = new ChatHistoryDto(chat.getId(), chat.getTitle(), messages);
        return ResponseEntity.ok(historyDto);
    }

    private UserContext authenticateAndGetUserContext(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }
        
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }

        String role = authService.getRoleFromToken(token);
        if (!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role) && !"ROLE_WORKER".equals(role)) {
            throw new AuthorizationException("Insufficient privileges");
        }

        String email = authService.getEmailFromToken(token);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return new UserContext(
            user.getId().toString(),
            user.getLogin().getRole().name(),
            user.getFullName()
        );
    }
}
