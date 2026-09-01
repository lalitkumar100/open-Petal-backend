package com.crimsonlogic.open_petal_backend.entity;


import com.crimsonlogic.open_petal_backend.enumerator.SkillLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "learning_goals",
        uniqueConstraints = @UniqueConstraint(name = "unique_user_learning_goal", columnNames = {"user_id", "skill_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The learner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The skill they want to learn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // Where they are now vs where they want to be
    @Enumerated(EnumType.STRING)
    @Column(name = "current_level", nullable = false, length = 20)
    @Builder.Default
    private SkillLevel currentLevel = SkillLevel.BEGINNER;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_level", nullable = false, length = 20)
    @Builder.Default
    private SkillLevel targetLevel = SkillLevel.INTERMEDIATE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}