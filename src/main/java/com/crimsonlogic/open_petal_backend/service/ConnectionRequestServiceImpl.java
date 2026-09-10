package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.connection.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.entity.ChatMessage;
import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConnectionStatus;
import com.crimsonlogic.open_petal_backend.enums.MessageStatus;
import com.crimsonlogic.open_petal_backend.repository.ChatMessageRepository;
import com.crimsonlogic.open_petal_backend.repository.ConnectionRequestRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for connection request operations.
 */
@Service
@RequiredArgsConstructor
public class ConnectionRequestServiceImpl implements ConnectionRequestService {

    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, persists changes to the database, and maps the result to a Data Transfer Object (DTO).
     */
    @Override
    @Transactional
    public ConnectionRequest sendConnectionRequest(Long senderId, SendConnectionRequestDto dto) {
        if (connectionRequestRepository.existsBySenderIdAndReceiverId(senderId, dto.getReceiverId())) {
            /**
             * Runtime Exception.
             */
            throw new RuntimeException("You have already sent a connection request to this user.");
        }

        User sender = userRepository.findById(senderId)
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(dto.getReceiverId())
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Skill primarySkill = skillRepository.findById(dto.getPrimarySkillId())
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new RuntimeException("Primary skill not found"));

        Skill offeredSkill = null;
        if (dto.getOfferedSkillId() != null) {
            offeredSkill = skillRepository.findById(dto.getOfferedSkillId())
                    /**
                     * Or Else Throw.
                     */
                    .orElseThrow(() -> new RuntimeException("Offered skill not found"));
        }

        ConnectionRequest request = ConnectionRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .primarySkill(primarySkill)
                .offeredSkill(offeredSkill)
                .initialMessage(dto.getInitialMessage())
                .status(ConnectionStatus.PENDING)
                /**
                 * Build.
                 */
                .build();

        /**
         * Save.
         */
        ConnectionRequest savedRequest = connectionRequestRepository.save(request);

        // Also add the initial message to the chat table
        if (dto.getInitialMessage() != null && !dto.getInitialMessage().trim().isEmpty()) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .connectionRequest(savedRequest)
                    .content(dto.getInitialMessage())
                    .status(MessageStatus.SENT)
                    /**
                     * Build.
                     */
                    .build();
            /**
             * Save.
             */
            chatMessageRepository.save(chatMessage);
        }

        return savedRequest;
    }

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, and persists changes to the database.
     */
    @Override
    @Transactional
    public ConnectionRequest acceptConnectionRequest(Long receiverId, Long requestId) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            /**
             * Runtime Exception.
             */
            throw new RuntimeException("You are not authorized to accept this request");
        }

        if (!ConnectionStatus.PENDING.equals(request.getStatus())) {
            /**
             * Runtime Exception.
             */
            throw new RuntimeException("Connection request is not in PENDING state");
        }

        /**
         * Set Status.
         */
        request.setStatus(ConnectionStatus.ACCEPTED);
        /**
         * Save.
         */
        ConnectionRequest savedRequest = connectionRequestRepository.save(request);

        // Ensure at least one chat message exists so the conversation is shown
        java.util.List<ChatMessage> existingMessages = chatMessageRepository.findByConnectionRequestIdOrderByCreatedAtAsc(requestId);
        if (existingMessages.isEmpty()) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .sender(request.getReceiver())
                    .receiver(request.getSender())
                    .connectionRequest(savedRequest)
                    .content("hello this " + request.getReceiver().getFullName() + " nice to connect will you")
                    .status(MessageStatus.SENT)
                    .build();
            chatMessageRepository.save(chatMessage);
        }

        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.crimsonlogic.open_petal_backend.dto.connection.ConnectionRequestResponseDto> getSentConnectionRequests(Long senderId) {
        return connectionRequestRepository.findBySenderIdOrderByCreatedAtDesc(senderId).stream().map(req -> {
            String receiverName = req.getReceiver().getFullName() != null ? req.getReceiver().getFullName() : "Unknown";
            String initials = getInitials(receiverName);
            return com.crimsonlogic.open_petal_backend.dto.connection.ConnectionRequestResponseDto.builder()
                    .id(req.getId())
                    .receiverId(req.getReceiver().getId())
                    .receiverName(receiverName)
                    .receiverInitials(initials)
                    .primarySkillName(req.getPrimarySkill() != null ? req.getPrimarySkill().getName() : null)
                    .offeredSkillName(req.getOfferedSkill() != null ? req.getOfferedSkill().getName() : null)
                    .status(req.getStatus())
                    .createdAt(req.getCreatedAt())
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.crimsonlogic.open_petal_backend.dto.connection.ConnectionRequestResponseDto> getPendingReceivedRequests(Long receiverId) {
        return connectionRequestRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(receiverId, ConnectionStatus.PENDING).stream().map(req -> {
            String senderName = req.getSender().getFullName() != null ? req.getSender().getFullName() : "Unknown";
            String initials = getInitials(senderName);
            return com.crimsonlogic.open_petal_backend.dto.connection.ConnectionRequestResponseDto.builder()
                    .id(req.getId())
                    .receiverId(req.getSender().getId()) // using receiverId field to store the other user's ID
                    .receiverName(senderName)
                    .receiverInitials(initials)
                    .primarySkillName(req.getPrimarySkill() != null ? req.getPrimarySkill().getName() : null)
                    .offeredSkillName(req.getOfferedSkill() != null ? req.getOfferedSkill().getName() : null)
                    .status(req.getStatus())
                    .createdAt(req.getCreatedAt())
                    .build();
        }).collect(java.util.stream.Collectors.toList());
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
