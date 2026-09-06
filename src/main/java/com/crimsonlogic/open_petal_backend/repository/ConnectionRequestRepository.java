package com.crimsonlogic.open_petal_backend.repository;

import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
    
    @Query("SELECT cr FROM ConnectionRequest cr WHERE (cr.sender.id = :user1Id AND cr.receiver.id = :user2Id) OR (cr.sender.id = :user2Id AND cr.receiver.id = :user1Id)")
    java.util.Optional<ConnectionRequest> findConnectionBetweenUsers(@org.springframework.data.repository.query.Param("user1Id") Long user1Id, @org.springframework.data.repository.query.Param("user2Id") Long user2Id);
}
