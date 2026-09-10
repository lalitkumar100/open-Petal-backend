package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.connection.ConnectionRequestResponseDto;
import com.crimsonlogic.open_petal_backend.dto.connection.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.entity.ChatMessage;
import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConnectionStatus;
import com.crimsonlogic.open_petal_backend.repository.ChatMessageRepository;
import com.crimsonlogic.open_petal_backend.repository.ConnectionRequestRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.service.ConnectionRequestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectionRequestServiceImpl Mockito Unit Tests")
class ConnectionRequestServiceImplTest {

    @Mock
    private ConnectionRequestRepository connectionRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ConnectionRequestServiceImpl connectionRequestService;

    /**
     * Purpose: Validates successful creation and persistence of a connection request with an initial message.
     * Preconditions: No duplicate request exists; sender, receiver, and skills are valid.
     * Expected Behavior: Saves ConnectionRequest and creates initial ChatMessage in repository.
     */
    @Test
    @DisplayName("Should successfully send a connection request with initial message")
    void givenValidData_whenSendConnectionRequest_thenCreatesRequestAndMessage() {
        // Arrange
        SendConnectionRequestDto dto = new SendConnectionRequestDto();
        dto.setReceiverId(2L);
        dto.setPrimarySkillId(10L);
        dto.setOfferedSkillId(11L);
        dto.setInitialMessage("Hi, I want to learn Java!");

        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        Skill primary = new Skill();
        primary.setId(10L);
        Skill offered = new Skill();
        offered.setId(11L);

        ConnectionRequest savedRequest = ConnectionRequest.builder()
                .id(100L)
                .sender(sender)
                .receiver(receiver)
                .primarySkill(primary)
                .offeredSkill(offered)
                .status(ConnectionStatus.PENDING)
                .build();

        given(connectionRequestRepository.existsBySenderIdAndReceiverId(1L, 2L)).willReturn(false);
        given(userRepository.findById(1L)).willReturn(Optional.of(sender));
        given(userRepository.findById(2L)).willReturn(Optional.of(receiver));
        given(skillRepository.findById(10L)).willReturn(Optional.of(primary));
        given(skillRepository.findById(11L)).willReturn(Optional.of(offered));
        given(connectionRequestRepository.save(any(ConnectionRequest.class))).willReturn(savedRequest);

        // Act
        ConnectionRequest result = connectionRequestService.sendConnectionRequest(1L, dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ConnectionStatus.PENDING);
        verify(connectionRequestRepository, times(1)).save(any(ConnectionRequest.class));
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    /**
     * Purpose: Prevents duplicate connection requests between the same sender and receiver.
     * Preconditions: Duplicate request exists in repository.
     * Expected Behavior: Throws RuntimeException.
     */
    @Test
    @DisplayName("Should throw exception when duplicate connection request is sent")
    void givenExistingRequest_whenSendConnectionRequest_thenThrowsRuntimeException() {
        // Arrange
        SendConnectionRequestDto dto = new SendConnectionRequestDto();
        dto.setReceiverId(2L);

        given(connectionRequestRepository.existsBySenderIdAndReceiverId(1L, 2L)).willReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> connectionRequestService.sendConnectionRequest(1L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("You have already sent a connection request");
    }

    /**
     * Purpose: Tests acceptance of pending request by authorized receiver.
     * Preconditions: Valid request ID, receiver matches request receiver ID, status is PENDING.
     * Expected Behavior: Updates status to ACCEPTED and auto-generates greeting message if history is empty.
     */
    @Test
    @DisplayName("Should accept connection request and auto-generate initial chat if empty")
    void givenPendingRequest_whenAcceptConnectionRequest_thenUpdatesStatusToAccepted() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        receiver.setFirstName("Bob");

        ConnectionRequest request = ConnectionRequest.builder()
                .id(100L)
                .sender(sender)
                .receiver(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        given(connectionRequestRepository.findById(100L)).willReturn(Optional.of(request));
        given(connectionRequestRepository.save(any(ConnectionRequest.class))).willReturn(request);
        given(chatMessageRepository.findByConnectionRequestIdOrderByCreatedAtAsc(100L)).willReturn(Collections.emptyList());

        // Act
        ConnectionRequest accepted = connectionRequestService.acceptConnectionRequest(2L, 100L);

        // Assert
        assertThat(accepted.getStatus()).isEqualTo(ConnectionStatus.ACCEPTED);
        verify(connectionRequestRepository, times(1)).save(request);
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    /**
     * Purpose: Prevents unauthorized users from accepting a connection request.
     * Preconditions: Receiver ID does not match request's receiver ID.
     * Expected Behavior: Throws RuntimeException.
     */
    @Test
    @DisplayName("Should throw exception when unauthorized user attempts acceptance")
    void givenWrongUser_whenAcceptConnectionRequest_thenThrowsRuntimeException() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        ConnectionRequest request = ConnectionRequest.builder()
                .id(100L)
                .sender(sender)
                .receiver(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        given(connectionRequestRepository.findById(100L)).willReturn(Optional.of(request));

        // Act & Assert
        assertThatThrownBy(() -> connectionRequestService.acceptConnectionRequest(99L, 100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("You are not authorized to accept this request");
    }

    /**
     * Purpose: Verifies listing sent connection requests for a user.
     * Preconditions: User has sent requests.
     * Expected Behavior: Returns mapped ConnectionRequestResponseDto list.
     */
    @Test
    @DisplayName("Should retrieve sent connection requests for user")
    void givenSenderId_whenGetSentConnectionRequests_thenReturnsDtoList() {
        // Arrange
        User receiver = new User();
        receiver.setId(2L);
        receiver.setFirstName("Carol");
        receiver.setLastName("Danvers");

        Skill skill = new Skill();
        skill.setName("Python");

        ConnectionRequest req = ConnectionRequest.builder()
                .id(50L)
                .receiver(receiver)
                .primarySkill(skill)
                .status(ConnectionStatus.PENDING)
                .build();

        given(connectionRequestRepository.findBySenderIdOrderByCreatedAtDesc(1L)).willReturn(List.of(req));

        // Act
        List<ConnectionRequestResponseDto> sent = connectionRequestService.getSentConnectionRequests(1L);

        // Assert
        assertThat(sent).hasSize(1);
        assertThat(sent.get(0).getReceiverName()).isEqualTo("Carol Danvers");
        assertThat(sent.get(0).getReceiverInitials()).isEqualTo("CD");
    }

    /**
     * Purpose: Verifies listing pending received connection requests for a user.
     * Preconditions: Receiver has pending requests.
     * Expected Behavior: Returns mapped ConnectionRequestResponseDto list.
     */
    @Test
    @DisplayName("Should retrieve pending received connection requests for user")
    void givenReceiverId_whenGetPendingReceivedRequests_thenReturnsDtoList() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        sender.setFirstName("Dave");

        ConnectionRequest req = ConnectionRequest.builder()
                .id(60L)
                .sender(sender)
                .status(ConnectionStatus.PENDING)
                .build();

        given(connectionRequestRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(2L, ConnectionStatus.PENDING))
                .willReturn(List.of(req));

        // Act
        List<ConnectionRequestResponseDto> pending = connectionRequestService.getPendingReceivedRequests(2L);

        // Assert
        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getReceiverName()).isEqualTo("Dave");
        assertThat(pending.get(0).getReceiverInitials()).isEqualTo("DA");
    }
}
