classDiagram

    %% =========================
    %% USER & AUTHENTICATION
    %% =========================

    class User {
        +Long id
        +Login login
        +String firstName
        +String lastName
        +String email
        +String phone
        +LocalDate dob
        +Gender gender
        +String description
        +List~UserSkill~ skillsOffered
        +List~LearningGoal~ learningGoals
        +List~AvailabilitySlot~ availableTimeInWeek
        +isOfValidAge()
    }

    class Login {
        +Long id
        +String email
        +String passwordHash
        +RoleType role
        +AccountStatus status
        +LocalDateTime lastLoginAt
        +String reason
        +hashPassword()
        +verifyPassword()
    }

    User "1" --> "1" Login : authentication


    %% =========================
    %% SKILL MANAGEMENT
    %% =========================

    class SkillCategory {
        +Long id
        +String name
        +String description
        +List~Skill~ skills
    }

    class Skill {
        +Long id
        +SkillCategory category
        +String name
        +String slug
        +String description
        +Boolean isActive
        +generateSlug()
    }

    SkillCategory "1" --> "0..*" Skill : contains


    %% =========================
    %% USER SKILLS
    %% =========================

    class UserSkill {
        +Long id
        +User user
        +Skill skill
        +SkillLevel level
    }

    User "1" --> "0..*" UserSkill : offers
    Skill "1" --> "0..*" UserSkill : offered by


    %% =========================
    %% LEARNING GOALS
    %% =========================

    class LearningGoal {
        +Long id
        +User user
        +Skill skill
        +SkillLevel currentLevel
        +SkillLevel targetLevel
        +Map~String,Object~ roadplan
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    User "1" --> "0..*" LearningGoal : has
    Skill "1" --> "0..*" LearningGoal : target skill


    %% =========================
    %% CONNECTION REQUEST
    %% =========================

    class ConnectionRequest {
        +Long id
        +User sender
        +User receiver
        +Skill primarySkill
        +Skill offeredSkill
        +String initialMessage
        +ConnectionStatus status
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    User "1" --> "0..*" ConnectionRequest : sends
    User "1" --> "0..*" ConnectionRequest : receives

    Skill "1" --> "0..*" ConnectionRequest : primary skill
    Skill "0..1" --> "0..*" ConnectionRequest : offered skill


    %% =========================
    %% LEARNING SESSION
    %% =========================

    class LearningSession {
        +Long id
        +User learner
        +User mentor
        +SessionType sessionType
        +Skill primarySkill
        +Skill offeredSkill
        +LocalDate sessionDate
        +LocalTime startTime
        +LocalTime endTime
        +TeachingMode mode
        +Location location
        +String meetingLink
        +Integer creditsHeld
        +SessionStatus status
        +String learnerStartPassword
        +String mentorStartPassword
        +String learnerEndPassword
        +String mentorEndPassword
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    User "1" --> "0..*" LearningSession : learner
    User "1" --> "0..*" LearningSession : mentor

    Skill "1" --> "0..*" LearningSession : primary skill
    Skill "0..1" --> "0..*" LearningSession : offered skill

    Location "0..1" --> "0..*" LearningSession : used for offline


    %% =========================
    %% LOCATION
    %% =========================

    class Location {
        +Long id
        +String addressLine1
        +String addressLine2
        +String area
        +String city
        +String pincode
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }


    %% =========================
    %% SESSION CONFLICT
    %% =========================

    class SessionConflict {
        +Long id
        +LearningSession session
        +User raisedBy
        +String learnerStory
        +String mentorStory
        +ConflictStatus status
        +String adminResolutionNotes
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    LearningSession "1" --> "0..1" SessionConflict : conflict
    User "1" --> "0..*" SessionConflict : raises


    %% =========================
    %% SKILL INQUIRY
    %% =========================

    class SkillInquiry {
        +Long id
        +User sender
        +User receiver
        +Skill skill
        +String message
        +InquiryStatus status
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    User "1" --> "0..*" SkillInquiry : sends
    User "1" --> "0..*" SkillInquiry : receives
    Skill "1" --> "0..*" SkillInquiry : requested skill


    %% =========================
    %% USER QUERIES
    %% =========================

    class UserQuery {
        +Long id
        +User user
        +QueryType queryType
        +QueryStatus status
        +String subject
        +String description
        +String adminResponse
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    User "1" --> "0..*" UserQuery : submits