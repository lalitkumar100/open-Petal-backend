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
        return connectionRequestRepository.save(request);
    }
}
