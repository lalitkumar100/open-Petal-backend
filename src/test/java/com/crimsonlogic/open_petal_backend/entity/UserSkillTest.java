package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSkill Entity Tests")
class UserSkillTest {

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
        UserSkill entity = UserSkill.builder().build();
        assertThat(entity.getExperienceYears()).isEqualTo(0);
        assertThat(entity.getTeachingMode()).isEqualTo(TeachingMode.ONLINE);
        assertThat(entity.getSessionDurationMin()).isEqualTo(60);
        assertThat(entity.getCreditsPerSession()).isEqualTo(10);
        assertThat(entity.getLearnersTaughtCount()).isEqualTo(0);
        assertThat(entity.getAvgSkillRating()).isEqualTo(BigDecimal.valueOf(0.00));
        assertThat(entity.getIsDeleted()).isFalse();
    }
}
