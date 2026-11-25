package com.crimsonlogic.open_petal_backend.chat.service;

import com.crimsonlogic.open_petal_backend.chat.dto.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.chat.dto.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.chat.dto.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.chat.dto.UnreadCountResponseDto;
import java.util.List;

public interface ChatService {
    List<ConversationSummaryDto> getConversations(Long userId);
    List<ChatMessageDto> getMessages(Long connectionRequestId, Long userId);
    ChatMessageDto sendNewMessage(Long connectionRequestId, Long senderId, SendMessageRequestDto request);
    void markAsRead(Long connectionRequestId, Long userId);
    UnreadCountResponseDto getUnreadCount(Long userId);
}
