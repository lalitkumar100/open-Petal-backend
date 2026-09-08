package com.crimsonlogic.open_petal_backend.entity;

import com.crimsonlogic.open_petal_backend.enums.ConnectionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConnectionRequest Entity Tests")
class ConnectionRequestTest {

    /**
     * Purpose: Verifies default status is PENDING upon creation.
     * Preconditions: Connection request instantiated.
     * Expected Behavior: status is PENDING.
     */
    @Test
    @DisplayName("Should initialize with PENDING status")
    void givenNewRequest_whenInstantiated_thenStatusIsPending() {
        // Act
        ConnectionRequest request = ConnectionRequest.builder().build();

        // Assert
        assertThat(request.getStatus()).isEqualTo(ConnectionStatus.PENDING);
    }

    /**
     * Purpose: Verifies soft delete flag defaults to false.
     * Preconditions: Connection request instantiated.
     * Expected Behavior: isDeleted is false.
     */
    @Test
    @DisplayName("Should initialize with isDeleted false")
    void givenNewRequest_whenInstantiated_thenIsDeletedIsFalse() {
        // Act
        ConnectionRequest request = ConnectionRequest.builder().build();

        // Assert
        assertThat(request.getIsDeleted()).isFalse();
    }
    
    /**
     * Purpose: Tests builder with all relationships attached.
     * Preconditions: Mocked users and skills passed.
     * Expected Behavior: Relationships map successfully.
     */
    @Test
    @DisplayName("Should construct with full relations")
    void givenBuilder_whenRelationshipsAdded_thenMatches() {
        // Arrange
        User sender = new User();
        User receiver = new User();
        Skill primary = new Skill();
        
        // Act
        ConnectionRequest req = ConnectionRequest.builder()
            .sender(sender)
            .receiver(receiver)
            .primarySkill(primary)
            .initialMessage("Hi!")
            .build();
            
        // Assert
        assertThat(req.getSender()).isSameAs(sender);
        assertThat(req.getReceiver()).isSameAs(receiver);
        assertThat(req.getPrimarySkill()).isSameAs(primary);
        assertThat(req.getInitialMessage()).isEqualTo("Hi!");
    }
    
    /**
     * Purpose: Tests mutability of status.
     * Preconditions: Status changed to ACCEPTED.
     * Expected Behavior: Status updates successfully.
     */
    @Test
    @DisplayName("Should allow updating status to ACCEPTED")
    void givenRequest_whenAccepting_thenStatusChanges() {
        // Arrange
        ConnectionRequest req = ConnectionRequest.builder().build();
        
        // Act
        req.setStatus(ConnectionStatus.ACCEPTED); // Assuming ACCEPTED exists in enum
        
        // Assert
        assertThat(req.getStatus()).isEqualTo(ConnectionStatus.ACCEPTED);
    }
    
    /**
     * Purpose: Tests barter configuration.
     * Preconditions: Request has both primary and offered skills.
     * Expected Behavior: Both skills map properly.
     */
    @Test
    @DisplayName("Should store offered skill for barter requests")
    void givenBarterRequest_whenSettingOfferedSkill_thenStores() {
        // Arrange
        ConnectionRequest req = new ConnectionRequest();
        Skill offered = new Skill();
        
        // Act
        req.setOfferedSkill(offered);
        
        // Assert
        assertThat(req.getOfferedSkill()).isSameAs(offered);
    }
}
