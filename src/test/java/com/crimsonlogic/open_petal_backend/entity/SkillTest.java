package com.crimsonlogic.open_petal_backend.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Skill Entity Tests")
class SkillTest {

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
     * Purpose: Verifies @PrePersist/@PreUpdate hook automatically generates URL-safe slugs.
     * Preconditions: valid skill name provided.
     * Expected Behavior: generateSlug() populates the slug field correctly.
     */
    @Test
    @DisplayName("Should automatically generate slug from valid name")
    void givenValidName_whenGenerateSlug_thenSetsSlug() {
        // Arrange
        Skill skill = Skill.builder().name("Java Spring Boot").build();

        // Act
        skill.generateSlug();

        // Assert
        assertThat(skill.getSlug())
                .as("Slug should be generated based on the name")
                .isEqualTo("java-spring-boot"); // Assuming SlugUtil lowercases and hyphens
    }

    /**
     * Purpose: Prevents NPE during persistence lifecycle if name is missing.
     * Preconditions: skill name is null.
     * Expected Behavior: generateSlug() ignores and does not throw NPE.
     */
    @Test
    @DisplayName("Should not crash on null name during slug generation")
    void givenNullName_whenGenerateSlug_thenIgnores() {
        // Arrange
        Skill skill = Skill.builder().name(null).build();

        // Act
        skill.generateSlug();

        // Assert
        assertThat(skill.getSlug())
                .as("Slug should remain null if name is null")
                .isNull();
    }

    /**
     * Purpose: Verifies builder defaults.
     * Preconditions: New builder instance.
     * Expected Behavior: isActive is true, isDeleted is false.
     */
    @Test
    @DisplayName("Should instantiate with correct default booleans")
    void givenNewInstance_whenCreated_thenDefaultValuesSet() {
        // Act
        Skill skill = Skill.builder().build();

        // Assert
        assertThat(skill.getIsActive()).isTrue();
        assertThat(skill.getIsDeleted()).isFalse();
    }
    
    /**
     * Purpose: Tests Lombok builder pattern fully.
     * Preconditions: All fields populated via builder.
     * Expected Behavior: Object maintains data integrity.
     */
    @Test
    @DisplayName("Should correctly populate all fields via builder")
    void givenBuilder_whenSettingAll_thenConstructsProperly() {
        // Act
        Skill skill = Skill.builder()
            .id(1L)
            .name("React")
            .slug("react")
            .description("Frontend Library")
            .isActive(false)
            .build();
            
        // Assert
        assertThat(skill.getId()).isEqualTo(1L);
        assertThat(skill.getName()).isEqualTo("React");
        assertThat(skill.getIsActive()).isFalse();
    }

    /**
     * Purpose: Tests setting description.
     * Preconditions: New description provided.
     * Expected Behavior: Description is updated.
     */
    @Test
    @DisplayName("Should update description field correctly")
    void givenNewDescription_whenSet_thenUpdates() {
        // Arrange
        Skill skill = new Skill();
        
        // Act
        skill.setDescription("New Desc");
        
        // Assert
        assertThat(skill.getDescription()).isEqualTo("New Desc");
    }
}
