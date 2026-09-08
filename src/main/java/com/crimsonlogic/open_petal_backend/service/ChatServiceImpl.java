package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.chat.ChatMessageDto;
import com.crimsonlogic.open_petal_backend.dto.chat.ConversationSummaryDto;
import com.crimsonlogic.open_petal_backend.dto.chat.SendMessageRequestDto;
import com.crimsonlogic.open_petal_backend.dto.chat.UnreadCountResponseDto;
import com.crimsonlogic.open_petal_backend.entity.ChatMessage;
import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConnectionStatus;
import com.crimsonlogic.open_petal_backend.enums.MessageStatus;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.repository.ChatMessageRepository;
import com.crimsonlogic.open_petal_backend.repository.ConnectionRequestRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service interface for chat operations.
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<ConversationSummaryDto> getConversations(Long userId) {
        /**
         * Find Latest Messages For User.
         */
        List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessagesForUser(userId);
        
        return latestMessages.stream().map(msg -> {
            /**
             * Get Connection Request.
             */
            ConnectionRequest connection = msg.getConnectionRequest();
            /**
             * Get Sender.
             */
            User partner = connection.getSender().getId().equals(userId) ? connection.getReceiver() : connection.getSender();
            
            /**
             * Count By Connection Request Id And Receiver Id And Status Not.
             */
            long unreadCount = chatMessageRepository.countByConnectionRequestIdAndReceiverIdAndStatusNot(connection.getId(), userId, MessageStatus.READ);
            
            /**
             * Get Full Name.
             */
            String partnerName = partner.getFullName() != null ? partner.getFullName() : "Unknown";
            /**
             * Get Initials.
             */
            String partnerInitials = getInitials(partnerName);
            
            return ConversationSummaryDto.builder()
                    .connectionRequestId(connection.getId())
                    .partnerId(partner.getId())
                    .partnerName(partnerName)
                    .partnerInitials(partnerInitials)
                    .lastMessagePreview(msg.getContent())
                    .lastMessageTimestamp(msg.getCreatedAt())
                    .unreadCount(unreadCount)
                    /**
                     * Build.
                     */
                    .build();
        /**
         * Collect.
         */
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageDto> getMessages(Long connectionRequestId, Long userId) {
        ConnectionRequest connection = connectionRequestRepository.findById(connectionRequestId)
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new RuntimeException("Connection request not found"));
                
        if (!connection.getSender().getId().equals(userId) && !connection.getReceiver().getId().equals(userId)) {
            /**
             * Authorization Exception.
             */
            throw new AuthorizationException("Not a participant in this conversation");
        }
        
        /**
         * Find By Connection Request Id Order By Created At Asc.
         */
        List<ChatMessage> messages = chatMessageRepository.findByConnectionRequestIdOrderByCreatedAtAsc(connectionRequestId);
        /**
         * Stream.
         */
        return messages.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ChatMessageDto sendNewMessage(Long connectionRequestId, Long senderId, SendMessageRequestDto request) {
        ConnectionRequest connection = connectionRequestRepository.findById(connectionRequestId)
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        if (!ConnectionStatus.ACCEPTED.equals(connection.getStatus())) {
            /**
             * Runtime Exception.
             */
            throw new RuntimeException("Cannot send message. Connection is not active.");
        }

        if (!connection.getSender().getId().equals(senderId) && !connection.getReceiver().getId().equals(senderId)) {
            /**
             * Authorization Exception.
             */
            throw new AuthorizationException("Sender is not part of this connection.");
        }

        User sender = userRepository.findById(senderId)
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new RuntimeException("Sender not found"));
                
        /**
         * Get Sender.
         */
        User receiver = connection.getSender().getId().equals(senderId) ? connection.getReceiver() : connection.getSender();

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .connectionRequest(connection)
                .content(request.getContent())
                .status(MessageStatus.SENT)
                /**
                 * Build.
                 */
                .build();

        /**
         * Save.
         */
        ChatMessage savedMessage = chatMessageRepository.save(message);

        /**
         * Map To Dto.
         */
        return mapToDto(savedMessage);
    }

    /**
     * Data Flow: Executes the business logic by processing the input parameters.
     */
    @Override
    @Transactional
    public void markAsRead(Long connectionRequestId, Long userId) {
        /**
         * Update Status By Connection Request Id And Receiver Id.
         */
        chatMessageRepository.updateStatusByConnectionRequestIdAndReceiverId(MessageStatus.READ, connectionRequestId, userId);
    }

    @Override
    public UnreadCountResponseDto getUnreadCount(Long userId) {
        /**
         * Count By Receiver Id And Status Not.
         */
        long count = chatMessageRepository.countByReceiverIdAndStatusNot(userId, MessageStatus.READ);
        /**
         * Builder.
         */
        return UnreadCountResponseDto.builder().totalUnread(count).build();
    }
    
    /**
     * Data Flow: Executes the business logic by processing the input parameters.
     */
    private ChatMessageDto mapToDto(ChatMessage msg) {
        return ChatMessageDto.builder()
                .id(msg.getId())
                .connectionRequestId(msg.getConnectionRequest().getId())
                .senderId(msg.getSender().getId())
                .receiverId(msg.getReceiver().getId())
                .content(msg.getContent())
                .timestamp(msg.getCreatedAt())
                .status(msg.getStatus())
                /**
                 * Build.
                 */
                .build();
    }
    
    /**
     * Data Flow: Executes the business logic by processing the input parameters.
     */
    private String getInitials(String name) {
        /**
         * If.
         */
        if (name == null || name.trim().isEmpty()) return "?";
        /**
         * Trim.
         */
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            /**
             * Substring.
             */
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        /**
         * Return.
         */
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
