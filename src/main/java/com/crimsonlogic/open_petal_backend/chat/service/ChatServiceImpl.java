package com.crimsonlogic.open_petal_backend.chat.service;

import com.crimsonlogic.open_petal_backend.chat.dto.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.chat.dto.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.chat.dto.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.chat.dto.UnreadCountResponseDto;
import com.crimsonlogic.open_petal_backend.chat.entity.ChatMessage;
import com.crimsonlogic.open_petal_backend.chat.enums.MessageStatus;
import com.crimsonlogic.open_petal_backend.chat.repository.ChatMessageRepository;
import com.crimsonlogic.open_petal_backend.connection.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.connection.enums.ConnectionStatus;
import com.crimsonlogic.open_petal_backend.connection.repository.ConnectionRequestRepository;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.common.exception.BusinessException;
import com.crimsonlogic.open_petal_backend.common.exception.ResourceNotFoundException;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<ConversationSummaryDto> getConversations(Long userId) {
        List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessagesForUser(userId);
        
        return latestMessages.stream().map(msg -> {
            ConnectionRequest connection = msg.getConnectionRequest();
            User partner = connection.getSender().getId().equals(userId) ? connection.getReceiver() : connection.getSender();
            
            long unreadCount = chatMessageRepository.countByConnectionRequestIdAndReceiverIdAndStatusNot(connection.getId(), userId, MessageStatus.READ);
            
            String partnerName = partner.getFullName() != null ? partner.getFullName() : "Unknown";
            String partnerInitials = getInitials(partnerName);
            
            return ConversationSummaryDto.builder()
                    .connectionRequestId(connection.getId())
                    .partnerId(partner.getId())
                    .partnerName(partnerName)
                    .partnerInitials(partnerInitials)
                    .lastMessagePreview(msg.getContent())
                    .lastMessageTimestamp(msg.getCreatedAt())
                    .unreadCount(unreadCount)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageDto> getMessages(Long connectionRequestId, Long userId) {
        ConnectionRequest connection = connectionRequestRepository.findById(connectionRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));
                
        if (!connection.getSender().getId().equals(userId) && !connection.getReceiver().getId().equals(userId)) {
            throw new AuthorizationException("Not a participant in this conversation");
        }
        
        List<ChatMessage> messages = chatMessageRepository.findByConnectionRequestIdOrderByCreatedAtAsc(connectionRequestId);
        return messages.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ChatMessageDto sendNewMessage(Long connectionRequestId, Long senderId, SendMessageRequestDto request) {
        ConnectionRequest connection = connectionRequestRepository.findById(connectionRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        if (!ConnectionStatus.ACCEPTED.equals(connection.getStatus())) {
            throw new BusinessException("Connection is not active");
        }

        if (!connection.getSender().getId().equals(senderId) && !connection.getReceiver().getId().equals(senderId)) {
            throw new AuthorizationException("Sender is not part of this connection.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
                
        User receiver = connection.getSender().getId().equals(senderId) ? connection.getReceiver() : connection.getSender();

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .connectionRequest(connection)
                .content(request.getContent())
                .status(MessageStatus.SENT)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return mapToDto(savedMessage);
    }

    @Override
    @Transactional
    public void markAsRead(Long connectionRequestId, Long userId) {
        chatMessageRepository.updateStatusByConnectionRequestIdAndReceiverId(MessageStatus.READ, connectionRequestId, userId);
    }

    @Override
    public UnreadCountResponseDto getUnreadCount(Long userId) {
        long count = chatMessageRepository.countByReceiverIdAndStatusNot(userId, MessageStatus.READ);
        return UnreadCountResponseDto.builder().totalUnread(count).build();
    }
    
    private ChatMessageDto mapToDto(ChatMessage msg) {
        return ChatMessageDto.builder()
                .id(msg.getId())
                .connectionRequestId(msg.getConnectionRequest().getId())
                .senderId(msg.getSender().getId())
                .receiverId(msg.getReceiver().getId())
                .content(msg.getContent())
                .timestamp(msg.getCreatedAt())
                .status(msg.getStatus())
                .build();
    }
    
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
