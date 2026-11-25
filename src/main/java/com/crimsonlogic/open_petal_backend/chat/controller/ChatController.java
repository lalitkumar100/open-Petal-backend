package com.crimsonlogic.open_petal_backend.chat.controller;

import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.chat.dto.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.chat.dto.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.chat.dto.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.chat.dto.UnreadCountResponseDto;
import com.crimsonlogic.open_petal_backend.chat.service.ChatService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.common.exception.CustomException;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final AuthService authService;
    private final UserService userService;
    private final MessageService messageService;

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationSummaryDto>>> getConversations(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = getAuthenticatedUserId(authHeader);
        List<ConversationSummaryDto> conversations = chatService.getConversations(userId);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.CHAT_CONVERSATIONS_RETRIEVED,
                messageService.getMessage(MessageCodes.CHAT_CONVERSATIONS_RETRIEVED),
                conversations));
    }

    @GetMapping("/{connectionRequestId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getMessages(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long connectionRequestId) {
        Long userId = getAuthenticatedUserId(authHeader);
        List<ChatMessageDto> messages = chatService.getMessages(connectionRequestId, userId);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.CHAT_MESSAGES_RETRIEVED,
                messageService.getMessage(MessageCodes.CHAT_MESSAGES_RETRIEVED),
                messages));
    }

    @PostMapping("/{connectionRequestId}/send")
    public ResponseEntity<ApiResponse<ChatMessageDto>> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long connectionRequestId,
            @Valid @RequestBody SendMessageRequestDto request) {
        Long userId = getAuthenticatedUserId(authHeader);
        ChatMessageDto response = chatService.sendNewMessage(connectionRequestId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.CHAT_MESSAGE_SENT,
                messageService.getMessage(MessageCodes.CHAT_MESSAGE_SENT),
                response));
    }

    @PutMapping("/{connectionRequestId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long connectionRequestId) {
        Long userId = getAuthenticatedUserId(authHeader);
        chatService.markAsRead(connectionRequestId, userId);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.CHAT_MESSAGES_MARKED_READ,
                messageService.getMessage(MessageCodes.CHAT_MESSAGES_MARKED_READ),
                null));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponseDto>> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = getAuthenticatedUserId(authHeader);
        UnreadCountResponseDto response = chatService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.CHAT_UNREAD_COUNT_RETRIEVED,
                messageService.getMessage(MessageCodes.CHAT_UNREAD_COUNT_RETRIEVED),
                response));
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
}
