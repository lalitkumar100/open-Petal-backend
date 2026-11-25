package com.crimsonlogic.open_petal_backend.connection.service;

import com.crimsonlogic.open_petal_backend.chat.entity.ChatMessage;
import com.crimsonlogic.open_petal_backend.chat.enums.MessageStatus;
import com.crimsonlogic.open_petal_backend.chat.repository.ChatMessageRepository;
import com.crimsonlogic.open_petal_backend.connection.dto.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.connection.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.connection.enums.ConnectionStatus;
import com.crimsonlogic.open_petal_backend.connection.repository.ConnectionRequestRepository;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.common.exception.BusinessException;
import com.crimsonlogic.open_petal_backend.common.exception.DuplicateResourceException;
import com.crimsonlogic.open_petal_backend.common.exception.ResourceNotFoundException;
import com.crimsonlogic.open_petal_backend.skill.entity.Skill;
import com.crimsonlogic.open_petal_backend.skill.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConnectionRequestServiceImpl implements ConnectionRequestService {

    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public ConnectionRequest sendConnectionRequest(Long senderId, SendConnectionRequestDto dto) {
        if (connectionRequestRepository.existsBySenderIdAndReceiverId(senderId, dto.getReceiverId())) {
            throw new DuplicateResourceException("A connection request already exists");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Skill primarySkill = skillRepository.findById(dto.getPrimarySkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Primary skill not found"));

        Skill offeredSkill = null;
        if (dto.getOfferedSkillId() != null) {
            offeredSkill = skillRepository.findById(dto.getOfferedSkillId())
                    .orElseThrow(() -> new ResourceNotFoundException("Offered skill not found"));
        }

        ConnectionRequest request = ConnectionRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .primarySkill(primarySkill)
                .offeredSkill(offeredSkill)
                .initialMessage(dto.getInitialMessage())
                .status(ConnectionStatus.PENDING)
                .build();

        ConnectionRequest savedRequest = connectionRequestRepository.save(request);

        // Also add the initial message to the chat table
        if (dto.getInitialMessage() != null && !dto.getInitialMessage().trim().isEmpty()) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .connectionRequest(savedRequest)
                    .content(dto.getInitialMessage())
                    .status(MessageStatus.SENT)
                    .build();
            chatMessageRepository.save(chatMessage);
        }

        return savedRequest;
    }

    @Override
    @Transactional
    public ConnectionRequest acceptConnectionRequest(Long receiverId, Long requestId) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new AuthorizationException("Not authorized to accept this request");
        }

        if (!ConnectionStatus.PENDING.equals(request.getStatus())) {
            throw new BusinessException("Connection request is not pending");
        }

        request.setStatus(ConnectionStatus.ACCEPTED);
        return connectionRequestRepository.save(request);
    }
}
