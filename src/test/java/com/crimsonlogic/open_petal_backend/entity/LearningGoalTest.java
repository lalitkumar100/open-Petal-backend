package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LearningGoal Entity Tests")
class LearningGoalTest {

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
        LearningGoal entity = LearningGoal.builder().build();
        assertThat(entity.getCurrentLevel()).isEqualTo(SkillLevel.BEGINNER);
        assertThat(entity.getTargetLevel()).isEqualTo(SkillLevel.INTERMEDIATE);
        assertThat(entity.getIsDeleted()).isFalse();
    }
}
