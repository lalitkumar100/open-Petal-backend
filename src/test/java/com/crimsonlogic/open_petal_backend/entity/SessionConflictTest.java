package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SessionConflict Entity Tests")
class SessionConflictTest {

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

    @Test
    @DisplayName("Should instantiate with correct defaults")
    void shouldInstantiateWithDefaults() {
        SessionConflict entity = SessionConflict.builder().build();
        assertThat(entity.getStatus()).isEqualTo(ConflictStatus.OPEN);
    }
}
