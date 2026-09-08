package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.AiChat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AiChatRepository extends JpaRepository<AiChat, String> {
    List<AiChat> findByUserIdOrderByLastMessageAtDesc(String userId);
    Optional<AiChat> findByIdAndUserId(String id, String userId);
}
