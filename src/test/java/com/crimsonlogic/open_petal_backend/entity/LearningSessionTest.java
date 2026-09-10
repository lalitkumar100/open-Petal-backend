package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LearningSession Entity Tests")
class LearningSessionTest {

    /**
     * Purpose: Forces garbage collection before all tests for professional memory management.
     */
    @BeforeAll
    static void beforeAll() {
        System.gc();
    }

    /**
     * Purpose: Forces garbage collection after all tests to free up resources.
     */
    @AfterAll
    static void afterAll() {
        System.gc();
    }


    /**
     * Purpose: Verifies default status is REQUESTED upon creation.
     * Preconditions: Session instantiated.
     * Expected Behavior: status is REQUESTED.
     */
    @Test
    @DisplayName("Should initialize with REQUESTED status")
    void givenNewSession_whenInstantiated_thenStatusIsRequested() {
        // Act
        LearningSession session = LearningSession.builder().build();

        // Assert
        assertThat(session.getStatus()).isEqualTo(SessionStatus.REQUESTED);
    }

    /**
     * Purpose: Verifies boolean verification defaults are strictly false.
     * Preconditions: Session instantiated.
     * Expected Behavior: Handshake verifications are false.
     */
    @Test
    @DisplayName("Should initialize handshake flags to false")
    void givenNewSession_whenInstantiated_thenVerificationsAreFalse() {
        // Act
        LearningSession session = LearningSession.builder().build();

        // Assert
        assertThat(session.getLearnerVerifiedStart()).isFalse();
        assertThat(session.getMentorVerifiedStart()).isFalse();
        assertThat(session.getLearnerVerifiedEnd()).isFalse();
        assertThat(session.getMentorVerifiedEnd()).isFalse();
    }
    
    /**
     * Purpose: Tests setting passwords for two-way handshake.
     * Preconditions: Passwords are provided.
     * Expected Behavior: Passwords map successfully.
     */
    @Test
    @DisplayName("Should store handshake passwords")
    void givenPasswords_whenSetting_thenStoresCorrectly() {
        // Arrange
        LearningSession session = new LearningSession();
        
        // Act
        session.setLearnerStartPassword("A1B2");
        session.setMentorStartPassword("C3D4");
        
        // Assert
        assertThat(session.getLearnerStartPassword()).isEqualTo("A1B2");
        assertThat(session.getMentorStartPassword()).isEqualTo("C3D4");
    }
    
    /**
     * Purpose: Tests credit holdings initialization.
     * Preconditions: Credit based session.
     * Expected Behavior: Defaults to 0, updates appropriately.
     */
    @Test
    @DisplayName("Should default credits to 0 and allow updates")
    void givenNewSession_whenSettingCredits_thenUpdates() {
        // Arrange
        LearningSession session = LearningSession.builder().build();
        assertThat(session.getCreditsHeld()).isEqualTo(0);
        
        // Act
        session.setCreditsHeld(15);
        
        // Assert
        assertThat(session.getCreditsHeld()).isEqualTo(15);
    }
    
    /**
     * Purpose: Tests online meeting links.
     * Preconditions: Online teaching mode.
     * Expected Behavior: Meeting link gets stored.
     */
    @Test
    @DisplayName("Should store meeting links for online sessions")
    void givenOnlineSession_whenSettingLink_thenStores() {
        // Arrange
        LearningSession session = new LearningSession();
        
        // Act
        session.setMeetingLink("https://zoom.us/j/12345");
        
        // Assert
        assertThat(session.getMeetingLink()).isEqualTo("https://zoom.us/j/12345");
    }
}
