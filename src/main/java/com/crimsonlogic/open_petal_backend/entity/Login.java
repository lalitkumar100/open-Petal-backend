package com.crimsonlogic.open_petal_backend.entity;

import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import com.crimsonlogic.open_petal_backend.enumerator.RoleType;
import com.crimsonlogic.open_petal_backend.util.PasswordUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "logins",
    indexes = {
        @Index(name = "idx_login_email", columnList = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password hash cannot be empty")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoleType role = RoleType.ROLE_USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.BLOCKED;

    @Column(
            name = "last_login_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "reason", length = 255)
    private String reason;

    // Bi-directional 1:1 mapping back to User Profile
    @OneToOne(mappedBy = "login", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    public void hashPassword(String plainPassword) {
        this.passwordHash = PasswordUtil.generateHash(plainPassword);
    }

    public boolean verifyPassword(String plainPassword) {
        if (this.passwordHash == null || plainPassword == null) {
            return false;
        }
        return this.passwordHash.equals(PasswordUtil.generateHash(plainPassword));
    }
}
