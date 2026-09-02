package com.crimsonlogic.open_petal_backend.entity;

import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "learning_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private SessionType sessionType;

    // The skill the Mentor is teaching
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_skill_id", nullable = false)
    private Skill primarySkill;

    // The skill the Learner is teaching (ONLY used if SessionType = BARTER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_skill_id")
    private Skill offeredSkill;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 20)
    private TeachingMode mode;

    // Foreign Key mapping to Location table for OFFLINE sessions (Nullable if ONLINE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    // For ONLINE sessions
    @Column(name = "meeting_link", length = 2000)
    private String meetingLink;

    @Column(name = "credits_held", nullable = false)
    @Builder.Default
    private Integer creditsHeld = 0; // Will be 0 for Barter, >0 for Credit

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.REQUESTED;

    // --- 4 Passwords for the Two-Way Handshake ---
    @Column(name = "learner_start_password", length = 20)
    private String learnerStartPassword;

    @Column(name = "mentor_start_password", length = 20)
    private String mentorStartPassword;

    @Column(name = "learner_end_password", length = 20)
    private String learnerEndPassword;

    @Column(name = "mentor_end_password", length = 20)
    private String mentorEndPassword;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}