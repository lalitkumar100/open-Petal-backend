package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.chat.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.dto.chat.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.dto.chat.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.dto.chat.UnreadCountResponseDto;

import java.util.List;

public interface ChatService {
    List<ConversationSummaryDto> getConversations(Long userId);
    List<ChatMessageDto> getMessages(Long connectionRequestId, Long userId);
    ChatMessageDto sendNewMessage(Long connectionRequestId, Long senderId, SendMessageRequestDto request);
    void markAsRead(Long connectionRequestId, Long userId);
    UnreadCountResponseDto getUnreadCount(Long userId);
}
