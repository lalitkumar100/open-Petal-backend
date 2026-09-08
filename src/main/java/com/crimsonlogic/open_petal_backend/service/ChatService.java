package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.chat.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.dto.chat.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.dto.chat.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.dto.chat.UnreadCountResponseDto;

import java.util.List;

/**
 * Service interface for chat operations.
 */
public interface ChatService {
    /**
     * Get Conversations.
     */
    List<ConversationSummaryDto> getConversations(Long userId);
    /**
     * Get Messages.
     */
    List<ChatMessageDto> getMessages(Long connectionRequestId, Long userId);
    /**
     * Send New Message.
     */
    ChatMessageDto sendNewMessage(Long connectionRequestId, Long senderId, SendMessageRequestDto request);
    /**
     * Mark As Read.
     */
    void markAsRead(Long connectionRequestId, Long userId);
    /**
     * Get Unread Count.
     */
    UnreadCountResponseDto getUnreadCount(Long userId);
}
