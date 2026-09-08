package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, String> {
    List<AiChatMessage> findByChatIdOrderByCreatedAtAsc(String chatId);
}
