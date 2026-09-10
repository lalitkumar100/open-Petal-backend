package com.crimsonlogic.open_petal_backend.service.impl;

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
import com.crimsonlogic.open_petal_backend.service.ChatServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatServiceImpl Mockito Unit Tests")
class ChatServiceImplTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ConnectionRequestRepository connectionRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    /**
     * Purpose: Ensures conversation summaries are correctly mapped for a user with unread counts and partner initials.
     * Preconditions: Chat messages exist in database for the given user ID.
     * Expected Behavior: Returns a list of ConversationSummaryDto objects with formatted initials and preview messages.
     */
    @Test
    @DisplayName("Should retrieve active conversation summaries for a user")
    void givenUserId_whenGetConversations_thenReturnsConversationSummaryList() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        sender.setFirstName("Alice");
        sender.setLastName("Smith");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setFirstName("Bob");
        receiver.setLastName("Jones");

        ConnectionRequest connection = new ConnectionRequest();
        connection.setId(10L);
        connection.setSender(sender);
        connection.setReceiver(receiver);

        ChatMessage message = new ChatMessage();
        message.setId(100L);
        message.setConnectionRequest(connection);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Hello Bob!");
        message.setCreatedAt(LocalDateTime.now());

        given(chatMessageRepository.findLatestMessagesForUser(1L)).willReturn(List.of(message));
        given(chatMessageRepository.countByConnectionRequestIdAndReceiverIdAndStatusNot(10L, 1L, MessageStatus.READ)).willReturn(2L);

        // Act
        List<ConversationSummaryDto> result = chatService.getConversations(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPartnerId()).isEqualTo(2L);
        assertThat(result.get(0).getPartnerName()).isEqualTo("Bob Jones");
        assertThat(result.get(0).getPartnerInitials()).isEqualTo("BJ");
        assertThat(result.get(0).getUnreadCount()).isEqualTo(2L);
    }

    /**
     * Purpose: Validates fetching chat history for participants of a connection request.
     * Preconditions: Valid connection ID and participating user ID.
     * Expected Behavior: Returns formatted ChatMessageDto list.
     */
    @Test
    @DisplayName("Should retrieve messages for valid conversation participant")
    void givenValidConnectionAndUser_whenGetMessages_thenReturnsMessageList() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        ConnectionRequest connection = new ConnectionRequest();
        connection.setId(10L);
        connection.setSender(user1);
        connection.setReceiver(user2);

        ChatMessage msg = new ChatMessage();
        msg.setId(101L);
        msg.setConnectionRequest(connection);
        msg.setSender(user1);
        msg.setReceiver(user2);
        msg.setContent("Hi");
        msg.setStatus(MessageStatus.SENT);

        given(connectionRequestRepository.findById(10L)).willReturn(Optional.of(connection));
        given(chatMessageRepository.findByConnectionRequestIdOrderByCreatedAtAsc(10L)).willReturn(List.of(msg));

        // Act
        List<ChatMessageDto> messages = chatService.getMessages(10L, 1L);

        // Assert
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("Hi");
    }

    /**
     * Purpose: Prevents non-participants from retrieving chat messages.
     * Preconditions: User ID is neither sender nor receiver of connection request.
     * Expected Behavior: Throws AuthorizationException.
     */
    @Test
    @DisplayName("Should throw AuthorizationException when user is not a participant")
    void givenNonParticipantUser_whenGetMessages_thenThrowsAuthorizationException() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        ConnectionRequest connection = new ConnectionRequest();
        connection.setId(10L);
        connection.setSender(user1);
        connection.setReceiver(user2);

        given(connectionRequestRepository.findById(10L)).willReturn(Optional.of(connection));

        // Act & Assert
        assertThatThrownBy(() -> chatService.getMessages(10L, 99L))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("Not a participant in this conversation");
    }

    /**
     * Purpose: Verifies successful sending of a chat message in an active connection.
     * Preconditions: Connection status is ACCEPTED and sender exists.
     * Expected Behavior: Persists new ChatMessage and returns DTO.
     */
    @Test
    @DisplayName("Should send new message successfully when connection is active")
    void givenActiveConnection_whenSendNewMessage_thenSavesAndReturnsMessage() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        ConnectionRequest connection = new ConnectionRequest();
        connection.setId(10L);
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.ACCEPTED);

        SendMessageRequestDto request = new SendMessageRequestDto();
        request.setContent("Let's schedule a session!");

        ChatMessage savedMsg = ChatMessage.builder()
                .id(200L)
                .sender(sender)
                .receiver(receiver)
                .connectionRequest(connection)
                .content("Let's schedule a session!")
                .status(MessageStatus.SENT)
                .build();

        given(connectionRequestRepository.findById(10L)).willReturn(Optional.of(connection));
        given(userRepository.findById(1L)).willReturn(Optional.of(sender));
        given(chatMessageRepository.save(any(ChatMessage.class))).willReturn(savedMsg);

        // Act
        ChatMessageDto result = chatService.sendNewMessage(10L, 1L, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Let's schedule a session!");
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    /**
     * Purpose: Prevents messaging over unaccepted/pending connection requests.
     * Preconditions: Connection status is PENDING.
     * Expected Behavior: Throws RuntimeException with active connection message.
     */
    @Test
    @DisplayName("Should throw exception when trying to message on non-active connection")
    void givenPendingConnection_whenSendNewMessage_thenThrowsRuntimeException() {
        // Arrange
        ConnectionRequest connection = new ConnectionRequest();
        connection.setId(10L);
        connection.setStatus(ConnectionStatus.PENDING);

        given(connectionRequestRepository.findById(10L)).willReturn(Optional.of(connection));

        SendMessageRequestDto request = new SendMessageRequestDto();
        request.setContent("Hello");

        // Act & Assert
        assertThatThrownBy(() -> chatService.sendNewMessage(10L, 1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Connection is not active");
    }

    /**
     * Purpose: Verifies marking messages as read in batch for a given connection and user.
     * Preconditions: Valid connection and receiver user ID.
     * Expected Behavior: Invokes repository update method with MessageStatus.READ.
     */
    @Test
    @DisplayName("Should mark messages as read for receiver")
    void givenConnectionAndUser_whenMarkAsRead_thenUpdatesMessageStatus() {
        // Act
        chatService.markAsRead(10L, 2L);

        // Assert
        verify(chatMessageRepository, times(1))
                .updateStatusByConnectionRequestIdAndReceiverId(MessageStatus.READ, 10L, 2L);
    }

    /**
     * Purpose: Verifies calculation of total unread messages count for navbar/notifications.
     * Preconditions: User ID has unread messages in repository.
     * Expected Behavior: Returns UnreadCountResponseDto with total count.
     */
    @Test
    @DisplayName("Should return total unread count for user")
    void givenUserId_whenGetUnreadCount_thenReturnsTotalCount() {
        // Arrange
        given(chatMessageRepository.countByReceiverIdAndStatusNot(1L, MessageStatus.READ)).willReturn(5L);

        // Act
        UnreadCountResponseDto dto = chatService.getUnreadCount(1L);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getTotalUnread()).isEqualTo(5L);
    }
}
