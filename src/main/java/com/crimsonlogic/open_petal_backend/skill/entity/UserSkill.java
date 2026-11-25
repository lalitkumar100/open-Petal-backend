package com.crimsonlogic.open_petal_backend.skill.entity;

import com.crimsonlogic.open_petal_backend.skill.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.skill.enums.TeachingMode;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "user_skills",
        uniqueConstraints = @UniqueConstraint(name = "unique_user_offered_skill", columnNames = {"user_id", "skill_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who knows & teaches this skill
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The skill from the master catalog
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // How proficient the user is
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false, length = 20)
    private SkillLevel skillLevel;

    @Column(name = "experience_years", nullable = false)
    @Builder.Default
    private Integer experienceYears = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "teaching_mode", nullable = false, length = 20)
    @Builder.Default
    private TeachingMode teachingMode = TeachingMode.ONLINE;

    @Column(name = "session_duration_min", nullable = false)
    @Builder.Default
    private Integer sessionDurationMin = 60;

    // Variable rate (e.g., 5 to 20 credits/session)
    @Min(5)
    @Max(20)
    @Column(name = "credits_per_session", nullable = false)
    @Builder.Default
    private Integer creditsPerSession = 10;

    // Analytics counters for AI matching
    @Column(name = "learners_taught_count", nullable = false)
    @Builder.Default
    private Integer learnersTaughtCount = 0;

    @Column(name = "avg_skill_rating", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal avgSkillRating = BigDecimal.valueOf(0.00);

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    void JWTAuthenicationFilter(){

    }

}
