# class

```java

package com.skillexchange.model;

import com.skillexchange.util.SlugUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private SkillCategory category;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(length = 50)
    private String icon;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- AUTOMATIC SLUG GENERATION HOOKS ---
    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (this.name != null) {
            this.slug = SlugUtil.toSlug(this.name);
        }
    }
}

```

#### **SkillCategory**

```java
package com.skillexchange.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skill_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();
}
```

### 1. Java Enum Definition (`Gender.java`)

```java
package com.skillexchange.model.enums;

public enum Gender{
    MALE,
    FEMALE,
    OTHER
}
```

### 2. Updated Spring Data JPA Entity (`User.java`)

```java
package com.skillexchange.model;

import com.skillexchange.model.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id", nullable = false, unique = true)
    private Login login;

    @NotBlank(message = "First name is mandatory")
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "dob")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSkill> skillsOffered = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LearningGoal> learningGoals = new ArrayList<>();
}
```

### 

SQL

# 

```
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    auth_id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NULL,
    dob DATE NULL,
    gender VARCHAR(20) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_auth FOREIGN KEY (auth_id) REFERENCES logins(id) ON DELETE CASCADE,
    CONSTRAINT chk_user_gender CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER'))
);
```

### 5. Updated API Request & Response DTOs

#### Update Profile Request DTO (`UpdateProfileRequestDto.java`)

Java

# 

```java
package com.skillexchange.dto.user;

import com.skillexchange.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequestDto{

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "Invalid phone number format")
    private String phone;

    private LocalDate dob;

    private Gender gender; // Accepts "MALE", "FEMALE", or "OTHER"
}
```

#### 

# RoleType

```java
package com.skillexchange.model.enums;

public enum RoleType{
    ROLE_USER,
    ROLE_ADMIN
}
```

Java

# AccountStatus

```java
package com.skillexchange.model.enums;

public enum AccountStatus{
    ACTIVE,
    INACTIVE,
    BLOCKED
}
```

# Login

```java
package com.skillexchange.model;

import com.skillexchange.model.enums.AccountStatus;
import com.skillexchange.model.enums.RoleType;
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
public class Login{

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
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Bi-directional 1:1 mapping back to User Profile
    @OneToOne(mappedBy = "login", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;
}
```