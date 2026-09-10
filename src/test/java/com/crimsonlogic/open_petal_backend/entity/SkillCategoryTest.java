package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SkillCategory Entity Tests")
class SkillCategoryTest {

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
        SkillCategory entity = SkillCategory.builder().build();
        assertThat(entity).isNotNull();
        assertThat(entity.getIsDeleted()).isFalse();
    }
}
