package com.crimsonlogic.open_petal_backend.chat.repository;

import com.crimsonlogic.open_petal_backend.chat.entity.ChatMessage;
import com.crimsonlogic.open_petal_backend.chat.enums.MessageStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConnectionRequestIdOrderByCreatedAtAsc(Long connectionRequestId);

    long countByReceiverIdAndStatusNot(Long receiverId, MessageStatus status);

    long countByConnectionRequestIdAndReceiverIdAndStatusNot(Long connectionRequestId, Long receiverId, MessageStatus status);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.status = :status WHERE cm.connectionRequest.id = :connectionRequestId AND cm.receiver.id = :receiverId AND cm.status != :status")
    void updateStatusByConnectionRequestIdAndReceiverId(@Param("status") MessageStatus status, @Param("connectionRequestId") Long connectionRequestId, @Param("receiverId") Long receiverId);

    @Query("SELECT cm FROM ChatMessage cm WHERE (cm.sender.id = :userId OR cm.receiver.id = :userId) AND cm.id IN (SELECT MAX(c.id) FROM ChatMessage c GROUP BY c.connectionRequest.id) ORDER BY cm.createdAt DESC")
    List<ChatMessage> findLatestMessagesForUser(@Param("userId") Long userId);
}
