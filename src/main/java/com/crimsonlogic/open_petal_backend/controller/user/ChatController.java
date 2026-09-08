package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.chat.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.dto.chat.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.dto.chat.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.dto.chat.UnreadCountResponseDto;
import com.crimsonlogic.open_petal_backend.service.ChatService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing user chats and messaging.
 * Provides endpoints for retrieving conversations, sending messages, and tracking read status.
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final AuthService authService;
    private final UserService userService;

    /**
     * Retrieves all active chat conversations for the authenticated user.
     *
     * @param authHeader the authorization header containing the user token
     * @return ResponseEntity containing a list of ConversationSummaryDto
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Long userId = getAuthenticatedUserId(authHeader);
            List<ConversationSummaryDto> conversations = chatService.getConversations(userId);
            return ResponseEntity.ok(createSuccessResponse("Conversations fetched successfully", conversations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves messages for a specific connection request conversation.
     *
     * @param authHeader the authorization header containing the user token
     * @param connectionRequestId the unique identifier of the connection/conversation
     * @return ResponseEntity containing a list of ChatMessageDto
     */
    @GetMapping("/{connectionRequestId}/messages")
    public ResponseEntity<?> getMessages(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @PathVariable Long connectionRequestId) {
        try {
            Long userId = getAuthenticatedUserId(authHeader);
            List<ChatMessageDto> messages = chatService.getMessages(connectionRequestId, userId);
            return ResponseEntity.ok(createSuccessResponse("Messages fetched successfully", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Sends a new message in a specific conversation.
     *
     * @param authHeader the authorization header containing the user token
     * @param connectionRequestId the unique identifier of the conversation
     * @param request the payload containing the message text
     * @return ResponseEntity containing the created ChatMessageDto
     */
    @PostMapping("/{connectionRequestId}/send")
    public ResponseEntity<?> sendMessage(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @PathVariable Long connectionRequestId,
                                         @RequestBody SendMessageRequestDto request) {
        try {
            Long userId = getAuthenticatedUserId(authHeader);
            ChatMessageDto response = chatService.sendNewMessage(connectionRequestId, userId, request);
            return ResponseEntity.ok(createSuccessResponse("Message sent successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Marks all unread messages in a specific conversation as read.
     *
     * @param authHeader the authorization header containing the user token
     * @param connectionRequestId the unique identifier of the conversation
     * @return ResponseEntity indicating success
     */
    @PutMapping("/{connectionRequestId}/read")
    public ResponseEntity<?> markAsRead(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @PathVariable Long connectionRequestId) {
        try {
            Long userId = getAuthenticatedUserId(authHeader);
            chatService.markAsRead(connectionRequestId, userId);
            return ResponseEntity.ok(createSuccessResponse("Messages marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Retrieves the total count of unread messages for the authenticated user.
     *
     * @param authHeader the authorization header containing the user token
     * @return ResponseEntity containing an UnreadCountResponseDto
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Long userId = getAuthenticatedUserId(authHeader);
            UnreadCountResponseDto response = chatService.getUnreadCount(userId);
            return ResponseEntity.ok(createSuccessResponse("Unread count fetched successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    private Long getAuthenticatedUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }
        
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }

        String role = authService.getRoleFromToken(token);
        if (!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role)) {
            throw new AuthorizationException("Insufficient privileges");
        }

        String email = authService.getEmailFromToken(token);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return user.getId();
    }
    
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        return result;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }
}
