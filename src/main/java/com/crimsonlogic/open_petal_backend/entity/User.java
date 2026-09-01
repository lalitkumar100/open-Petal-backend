package com.crimsonlogic.open_petal_backend.entity;

import com.crimsonlogic.open_petal_backend.enumerator.Gender;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id", nullable = false, unique = true)
    private Login login;

    @NotBlank(message = "First name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name must contain only letters")
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name must contain only letters")
    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "dob")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    // Up to 300 characters including spaces
    @Size(max = 300, message = "Description cannot exceed 300 characters")
    @Column(name = "description", length = 300)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // 1. Skills the user knows & can teach
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSkill> skillsOffered = new ArrayList<>();

    // 2. Skills the user wants to learn
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LearningGoal> learningGoals = new ArrayList<>();

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "availability_slots", columnDefinition = "json")
    @Builder.Default
    private List<com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot> availableTimeInWeek = new ArrayList<>();


    @AssertTrue(message = "You must be at least 18 years old")
    public boolean isOfValidAge() {
        if (dob == null) {
            return false;
        }
        return java.time.Period.between(dob, LocalDate.now()).getYears() >= 18;
    }
}
