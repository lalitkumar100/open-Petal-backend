# Open Petal Backend — AI Development Context

## 1. Project Overview

**Project:** Open Petal

**Purpose:** A skill-exchange platform where users can:
- Create a profile.
- Add skills they can teach.
- Define skills they want to learn.
- Search for suitable users/mentors.
- Send connection requests.
- Propose skill barter/exchange.
- Create and attend learning sessions.
- Conduct sessions online or offline.
- Use a two-way password handshake to start/end sessions.
- Raise conflicts about sessions to an administrator.
- Submit general queries to the administrator.
- Submit skill-related inquiries.

The backend is implemented using **Java + Spring Boot + JPA/Hibernate**.

The domain model is centered around:
`User`, `Login`, `Skill`, `SkillCategory`, `UserSkill`, `LearningGoal`, `ConnectionRequest`, `LearningSession`, `Location`, `SessionConflict`, `SkillInquiry`, and `UserQuery`.

---

## 2. Core Business Concept

Open Petal supports two major learning models:

### Credit-based learning
A learner learns a skill from a mentor and uses platform credits.

For this model:
- `LearningSession.sessionType` identifies the session type.
- `creditsHeld` stores credits held for the session.
- `creditsHeld > 0` is expected for credit sessions.
- Credits are intended to be zero for barter sessions.

### Barter-based learning
Two users exchange knowledge.

Example:

> User A wants to learn Java from User B and offers React in return.

The session therefore has:
- `primarySkill` = skill being taught by the mentor.
- `offeredSkill` = skill being offered by the learner.

`offeredSkill` is nullable because it is only required for barter proposals.

---

# 3. Main Domain Entities

## 3.1 User

Table:

`users`

Purpose:
- Represents the user's public/profile information.
- Connects the user with authentication.
- Owns skills offered.
- Owns learning goals.
- Can act as learner or mentor.
- Can send/receive connection requests and inquiries.
- Can submit queries.
- Can raise session conflicts.

Important fields:

```text
id
auth_id
first_name
last_name
email
phone
dob
gender
description
availability_slots
created_at
updated_at
```

Relationships:

```text
User 1 ---- 1 Login
User 1 ---- * UserSkill
User 1 ---- * LearningGoal
User 1 ---- * ConnectionRequest (sender)
User 1 ---- * ConnectionRequest (receiver)
User 1 ---- * LearningSession (learner)
User 1 ---- * LearningSession (mentor)
User 1 ---- * SessionConflict (raisedBy)
User 1 ---- * SkillInquiry (sender)
User 1 ---- * SkillInquiry (receiver)
User 1 ---- * UserQuery
```

Age validation:
- User must be at least 18 years old.
- `dob` is required for this validation.

---

## 3.2 Login

Table:

`logins`

Purpose:
- Authentication credentials and account state.

Fields:

```text
id
email
password_hash
role
status
last_login_at
created_at
updated_at
reason
```

Relationships:

```text
Login 1 ---- 1 User
```

Important:
- Email is unique.
- Password is stored as a hash.
- Role is represented by `RoleType`.
- Account state is represented by `AccountStatus`.

Do not confuse `Login.email` with `User.email`.
Both currently exist in the entity model.

---

## 3.3 SkillCategory

Table:

`skill_categories`

Purpose:
- Groups related skills.

Fields:

```text
id
name
description
created_at
updated_at
```

Relationship:

```text
SkillCategory 1 ---- * Skill
```

---

## 3.4 Skill

Table:

`skills`

Purpose:
- Represents a skill available on the platform.

Fields:

```text
id
category_id
name
slug
description
is_active
created_at
updated_at
```

Relationships:

```text
SkillCategory 1 ---- * Skill
Skill 1 ---- * UserSkill
Skill 1 ---- * LearningGoal
Skill 1 ---- * ConnectionRequest (primarySkill)
Skill 1 ---- * ConnectionRequest (offeredSkill)
Skill 1 ---- * LearningSession (primarySkill)
Skill 1 ---- * LearningSession (offeredSkill)
Skill 1 ---- * SkillInquiry
```

Constraints:
- `name` is unique.
- `slug` is unique.
- `category_id` is mandatory.
- `is_active` defaults to `true`.

Slug behavior:
- `@PrePersist` and `@PreUpdate` generate the slug from the skill name.

---

## 3.5 UserSkill

Table:

`user_skills`

Purpose:
- Junction/domain entity representing skills that a user knows and can teach.

The exact complete class definition is not present in the supplied entity source, so do not invent additional fields without checking the actual class.

Known relationships:

```text
User 1 ---- * UserSkill
Skill 1 ---- * UserSkill
```

Conceptually:

```text
User <-- UserSkill --> Skill
```

---

## 3.6 LearningGoal

Table:

`learning_goals`

Purpose:
- Represents a skill that a user wants to learn.

Fields:

```text
id
user_id
skill_id
current_level
target_level
roadplan
created_at
updated_at
```

Relationships:

```text
User 1 ---- * LearningGoal
Skill 1 ---- * LearningGoal
```

Constraint:

```text
UNIQUE(user_id, skill_id)
```

This prevents the same user from creating duplicate learning goals for the same skill.

`roadplan` is stored as JSON.

---

# 4. Connection and Matching

## 4.1 ConnectionRequest

Table:

`connection_requests`

Purpose:
- Represents a user asking another user to connect around a learning opportunity.

Fields:

```text
id
sender_id
receiver_id
primary_skill_id
offered_skill_id
initial_message
status
created_at
updated_at
```

Relationships:

```text
User 1 ---- * ConnectionRequest : sender
User 1 ---- * ConnectionRequest : receiver
Skill 1 ---- * ConnectionRequest : primarySkill
Skill 0..1 ---- * ConnectionRequest : offeredSkill
```

Meaning:

- `sender` = user initiating the request.
- `receiver` = user receiving the request.
- `primarySkill` = skill the sender wants to learn.
- `offeredSkill` = optional skill the sender offers in exchange.
- `status` = request lifecycle.

Expected request lifecycle can include:

```text
PENDING
ACCEPTED
REJECTED
```

Use the actual `ConnectionStatus` enum as the source of truth if it differs.

---

# 5. Learning Sessions

## 5.1 LearningSession

Table:

`learning_sessions`

Purpose:
- Represents an actual scheduled learning session between two users.

Fields:

```text
id
learner_id
mentor_id
session_type
primary_skill_id
offered_skill_id
session_date
start_time
end_time
mode
location_id
meeting_link
credits_held
status

learner_start_password
mentor_start_password
learner_end_password
mentor_end_password

created_at
updated_at
```

Relationships:

```text
User 1 ---- * LearningSession : learner
User 1 ---- * LearningSession : mentor

Skill 1 ---- * LearningSession : primarySkill
Skill 0..1 ---- * LearningSession : offeredSkill

Location 0..1 ---- * LearningSession
```

### Learner vs Mentor

These are two different roles played by `User`.

```text
learner_id -> User
mentor_id  -> User
```

Never model learner and mentor as separate user tables.

---

## 5.2 Session Type

`sessionType` determines the learning model.

Conceptually:

```text
CREDIT
BARTER
```

Use the actual `SessionType` enum values in implementation.

For barter:
- `offeredSkill` should normally be populated.
- `creditsHeld` should be 0.

For credit:
- `creditsHeld` can be greater than 0 according to the business rules.

---

## 5.3 Teaching Mode

`mode` determines whether the session is online or offline.

Conceptually:

```text
ONLINE
OFFLINE
```

For online sessions:
- `meetingLink` is used.

For offline sessions:
- `location_id` points to `locations`.

Do not require an offline location for online sessions.

---

## 5.4 Session Status

`status` uses `SessionStatus`.

The entity currently defaults to:

```text
REQUESTED
```

Use the actual enum values when implementing transitions.

The session lifecycle should be treated as a state machine. Do not arbitrarily modify status values from controllers without applying the relevant business rules.

---

# 6. Location

Table:

`locations`

Purpose:
- Stores physical locations for offline learning sessions.

Fields:

```text
id
address_line_1
address_line_2
area
city
pincode
created_at
updated_at
```

Relationship:

```text
Location 1 ---- * LearningSession
```

A learning session's location is nullable because online sessions do not require a physical location.

---

# 7. Session Conflict

## 7.1 SessionConflict

Table:

`session_conflicts`

Purpose:
- Allows a user to report a problem/dispute concerning a learning session.
- Allows the administrator to investigate and resolve the conflict.

Fields:

```text
id
session_id
raised_by_user_id
learner_story
mentor_story
status
admin_resolution_notes
created_at
updated_at
```

Relationships:

```text
LearningSession 1 ---- 0..1 SessionConflict
User 1 ---- * SessionConflict
```

Important:
- `session_id` is unique.
- Therefore a session can have at most one conflict record.

Conflict lifecycle is represented by:

```text
ConflictStatus
```

Use the actual enum values as the source of truth.

### Important implementation warning

The supplied source contains `SessionConflict` twice:
1. A top-level `SessionConflict` entity.
2. A nested `SessionConflict` entity inside `User`.

Both point to the same `session_conflicts` table.

This should be cleaned up so there is only one JPA entity mapping for `session_conflicts`.

Prefer the top-level `SessionConflict` entity and remove the duplicated nested entity.

---

# 8. Skill Inquiry

## 8.1 SkillInquiry

Table:

`skill_inquiries`

Purpose:
- Represents a user asking another user about learning a particular skill.

Fields:

```text
id
sender_id
receiver_id
skill_id
message
status
created_at
updated_at
```

Relationships:

```text
User 1 ---- * SkillInquiry : sender
User 1 ---- * SkillInquiry : receiver
Skill 1 ---- * SkillInquiry
```

Important:
- `sender` is the learner/requesting user.
- `receiver` is the mentor/receiving user.
- `skill` identifies the skill involved.
- `message` is mandatory.
- Default status is `PENDING`.

Indexes currently defined:

```text
(receiver_id, status)
(sender_id)
```

---

# 9. User Query

## 9.1 UserQuery

Table:

`user_queries`

Purpose:
- General support/query system between users and administrators.

Fields:

```text
id
user_id
query_type
status
subject
description
admin_response
created_at
updated_at
```

Relationship:

```text
User 1 ---- * UserQuery
```

Typical use cases:
- General support.
- Account problems.
- Platform issues.
- Other administrative requests.

`QueryType` and `QueryStatus` enums should be treated as the source of truth.

---

# 10. Complete Database ER Structure

The core schema is:

```text
                         ┌─────────────────┐
                         │     LOGINS      │
                         └────────┬────────┘
                                  │ 1:1
                                  ▼
                         ┌─────────────────┐
                         │      USERS      │
                         └────────┬────────┘
                                  │
          ┌───────────────────────┼────────────────────────┐
          │                       │                        │
          ▼                       ▼                        ▼
   ┌─────────────┐        ┌──────────────┐       ┌───────────────────┐
   │ USER_SKILLS │        │LEARNING_GOALS│       │CONNECTION_REQUESTS│
   └──────┬──────┘        └──────┬───────┘       └─────────┬─────────┘
          │                      │                         │
          └───────────┬──────────┴─────────────────────────┘
                      ▼
                ┌─────────────┐
                │   SKILLS    │
                └──────┬──────┘
                       │
                       ▼
               ┌─────────────────┐
               │SKILL_CATEGORIES │
               └─────────────────┘


                         USERS
                           │
                   ┌───────┴────────┐
                   │                │
                learner           mentor
                   │                │
                   └───────┬────────┘
                           ▼
                  ┌──────────────────┐
                  │LEARNING_SESSIONS │
                  └────────┬─────────┘
                           │
                 ┌─────────┴──────────┐
                 │                    │
                 ▼                    ▼
          ┌─────────────┐     ┌──────────────────┐
          │  LOCATIONS  │     │SESSION_CONFLICTS │
          └─────────────┘     └──────────────────┘


          USERS ──────────────► SKILL_INQUIRIES
            │
            └───────────────► USER_QUERIES
```

---

# 11. Foreign Key Summary

| Table | Foreign Key | References |
|---|---|---|
| users | auth_id | logins.id |
| skills | category_id | skill_categories.id |
| user_skills | user_id | users.id |
| user_skills | skill_id | skills.id |
| learning_goals | user_id | users.id |
| learning_goals | skill_id | skills.id |
| connection_requests | sender_id | users.id |
| connection_requests | receiver_id | users.id |
| connection_requests | primary_skill_id | skills.id |
| connection_requests | offered_skill_id | skills.id |
| learning_sessions | learner_id | users.id |
| learning_sessions | mentor_id | users.id |
| learning_sessions | primary_skill_id | skills.id |
| learning_sessions | offered_skill_id | skills.id |
| learning_sessions | location_id | locations.id |
| session_conflicts | session_id | learning_sessions.id |
| session_conflicts | raised_by_user_id | users.id |
| skill_inquiries | sender_id | users.id |
| skill_inquiries | receiver_id | users.id |
| skill_inquiries | skill_id | skills.id |
| user_queries | user_id | users.id |

---

# 12. Application Workflow

## User Registration

```text
User Registration
       │
       ▼
Create Login
       │
       ▼
Create User Profile
       │
       ▼
Add Skills Offered
       │
       ▼
Add Learning Goals
       │
       ▼
User is ready for matching
```

---

## Find a Learning Partner

```text
User
 │
 ▼
Search / Recommendation
 │
 ▼
Find users based on skills
 │
 ▼
View User Profile
 │
 ▼
Select Learning Skill
 │
 ▼
Create ConnectionRequest
 │
 ▼
Receiver accepts/rejects
 │
 ├── REJECTED → End
 │
 └── ACCEPTED
          │
          ▼
     Continue interaction
```

---

## Barter Flow

```text
Learner
   │
   │ Wants Java
   ▼
Mentor with Java
   │
   │ Learner offers React
   ▼
ConnectionRequest
   │
   ├── primarySkill = Java
   └── offeredSkill = React
          │
          ▼
      Accepted
          │
          ▼
   Schedule LearningSession
          │
          ▼
     BARTER session
```

---

## Credit Session Flow

```text
Learner
   │
   ▼
Select Mentor + Skill
   │
   ▼
Schedule Session
   │
   ▼
Hold Credits
   │
   ▼
LearningSession
   │
   ▼
Complete Session
   │
   ▼
Apply credit/business settlement rules
```

---

## Online Session

```text
LearningSession
      │
      ├── mode = ONLINE
      │
      └── meeting_link = provided
```

## Offline Session

```text
LearningSession
      │
      ├── mode = OFFLINE
      │
      └── location_id
               │
               ▼
           Location
```

---

# 13. Two-Way Session Handshake

Each session contains four passwords:

```text
learner_start_password
mentor_start_password

learner_end_password
mentor_end_password
```

Conceptually:

### Start

```text
Learner provides learner_start_password
             +
Mentor provides mentor_start_password
             │
             ▼
        Verify both
             │
             ▼
       Start session
```

### End

```text
Learner provides learner_end_password
             +
Mentor provides mentor_end_password
             │
             ▼
        Verify both
             │
             ▼
       Complete session
```

Do not expose these passwords unnecessarily through API responses.

---

# 14. Conflict Resolution Workflow

```text
Learning Session
      │
      ▼
User detects problem
      │
      ▼
Raise Conflict
      │
      ▼
SessionConflict
      │
      ├── raisedBy
      ├── learnerStory
      ├── mentorStory
      └── status = OPEN
              │
              ▼
          Admin Review
              │
              ▼
       Admin Resolution
              │
              ├── adminResolutionNotes
              └── update ConflictStatus
```

The system should preserve both sides of the story where applicable.

---

# 15. Skill Management Workflow

A skill belongs to a category.

```text
SkillCategory
      │
      ├── Java
      ├── Python
      ├── React
      ├── Angular
      └── etc.
```

Each skill has:
- Name.
- Unique slug.
- Description.
- Active/inactive state.

Users can:
- Offer a skill through `UserSkill`.
- Request/target a skill through `LearningGoal`.

---

# 16. Admin Responsibilities

The administrator is expected to manage platform-level issues such as:

### Skill management
- Review skill-related requests/inquiries.
- Manage skill categories.
- Manage active/inactive skills.

### Conflict management
- Review `SessionConflict`.
- Review learner and mentor stories.
- Add resolution notes.
- Resolve conflicts.

### User queries
- Review `UserQuery`.
- Respond to users.
- Update query status.

Do not introduce additional admin tables unless the implementation requires them.

The current model represents administrator actions primarily through:
- `Login.role`
- `UserQuery.adminResponse`
- `SessionConflict.adminResolutionNotes`

---

# 17. API/Service Design Guidance

Use a layered Spring Boot architecture.

Recommended structure:

```text
controller/
service/
repository/
entity/
dto/
mapper/
exception/
config/
enums/
util/
```

### Controller
Responsible for:
- HTTP request/response.
- Validation.
- Authentication/authorization boundaries.
- Calling services.

Do not place complex business logic inside controllers.

### Service
Responsible for:
- Business rules.
- State transitions.
- Transaction boundaries.
- Coordinating repositories.

### Repository
Responsible for:
- Database access.
- JPA queries.
- Entity persistence.

### DTO
Use DTOs for API contracts.

Avoid exposing JPA entities directly when the endpoint contains sensitive fields or nested relationships.

---

# 18. Important Business Rules

1. A user must be at least 18 years old.
2. A skill belongs to exactly one skill category.
3. A user's learning goal references one skill.
4. A user cannot have duplicate learning goals for the same skill.
5. A learning session has exactly one learner and one mentor.
6. Learner and mentor are both `User` records.
7. A learning session has one primary skill.
8. An offered skill is optional and is mainly used for barter.
9. Online sessions use a meeting link.
10. Offline sessions can reference a physical location.
11. Credit sessions can hold credits.
12. Barter sessions should not require credits.
13. A session can have at most one conflict.
14. A conflict records who raised it.
15. A conflict can contain both learner and mentor explanations.
16. Administrators can add resolution notes.
17. Connection requests have sender and receiver roles.
18. Skill inquiries have sender and receiver roles.
19. Email fields marked unique must remain unique.
20. Skill name and slug must remain unique.
21. Do not create duplicate JPA mappings for `session_conflicts`.
22. Do not expose password hashes or session handshake passwords in normal API responses.

---

# 19. Enum Source of Truth

The project currently references these enums:

```text
Gender
RoleType
AccountStatus
SkillLevel
SessionType
TeachingMode
SessionStatus
ConnectionStatus
ConflictStatus
InquiryStatus
QueryType
QueryStatus
```

When implementing logic:
- Use the enum definitions already present in the project.
- Do not invent new enum values if the existing enum does not contain them.
- If a workflow needs a state that is not represented by an enum, update the enum deliberately and consistently across database, entity, service, DTO, and API layers.

---

# 20. Coding Rules for AI

When modifying this project:

### First inspect
Before creating or changing code, inspect:
1. Existing entity classes.
2. Existing enums.
3. Existing repositories.
4. Existing services.
5. Existing controllers.
6. DTOs.
7. Security configuration.
8. Database schema/migrations.
9. Existing exception handling.

### Do not blindly create duplicates
Before creating a class:
- Search for an existing class with the same responsibility.
- Reuse existing DTOs/services/repositories when appropriate.

### Preserve existing naming
Use the existing naming conventions.

Examples:

```text
LearningSession
ConnectionRequest
LearningGoal
SessionConflict
SkillInquiry
UserQuery
```

Do not rename these entities casually.

### JPA rules
Respect existing mappings:
- `@OneToOne`
- `@OneToMany`
- `@ManyToOne`
- `@JoinColumn`
- `mappedBy`
- unique constraints
- nullable constraints
- cascade behavior
- orphan removal

Do not change relationships simply to make compilation easier.

### Database changes
When changing an entity:
- Check the corresponding database schema/migration.
- Check foreign keys.
- Check unique constraints.
- Check nullable columns.
- Check enum persistence.
- Check existing data compatibility.

---

# 21. Security Rules

Authentication-related data includes:

```text
Login.passwordHash
```

Session handshake secrets include:

```text
learnerStartPassword
mentorStartPassword
learnerEndPassword
mentorEndPassword
```

Treat all of these as sensitive.

Never:
- Log raw passwords.
- Return password hashes in API responses.
- Return handshake passwords unnecessarily.
- Store plain authentication passwords.

Use secure password hashing for user authentication.

---

# 22. Relationship Diagram

```text
LOGIN
  │
  │ 1:1
  ▼
USER
 │
 ├──────────────► USER_SKILL ─────────► SKILL ─────────► SKILL_CATEGORY
 │
 ├──────────────► LEARNING_GOAL ──────► SKILL
 │
 ├──────────────► CONNECTION_REQUEST ─► SKILL
 │                         │
 │                         └──────────► SKILL (offered)
 │
 ├──────────────► LEARNING_SESSION ───► SKILL (primary)
 │                         │
 │                         ├──────────► SKILL (offered)
 │                         ├──────────► LOCATION
 │                         └──────────► SESSION_CONFLICT
 │
 ├──────────────► SKILL_INQUIRY ──────► SKILL
 │
 └──────────────► USER_QUERY
```

---

# 23. Current Entity Source Notes

The supplied source confirms the following mappings:

- `LearningSession` maps to `learning_sessions`.
- `ConnectionRequest` maps to `connection_requests`.
- `LearningGoal` maps to `learning_goals`.
- `Location` maps to `locations`.
- `Login` maps to `logins`.
- `SessionConflict` maps to `session_conflicts`.
- `Skill` maps to `skills`.
- `SkillCategory` maps to `skill_categories`.
- `SkillInquiry` maps to `skill_inquiries`.
- `User` maps to `users`.
- `UserQuery` maps to `user_queries`.

The source also confirms the primary user/skill/session relationships and the JSON fields `availability_slots` and `roadplan`.

`UserSkill` is referenced by `User`, but its complete entity definition was not included in the supplied source. Therefore its exact columns should be obtained from the actual project before generating SQL or DTOs for it.

---

# 24. AI Task Execution Strategy

When asked to implement a feature:

```text
1. Understand the business requirement
        ↓
2. Identify affected entities
        ↓
3. Check existing enums
        ↓
4. Check repositories
        ↓
5. Check services
        ↓
6. Check DTOs
        ↓
7. Check controllers
        ↓
8. Check database/migrations
        ↓
9. Implement the smallest consistent change
        ↓
10. Validate relationships and constraints
        ↓
11. Check compilation
        ↓
12. Check API behavior
```

For bug fixing:

```text
Error
 ↓
Find exact source
 ↓
Understand entity/DB mapping
 ↓
Check generated SQL / FK / enum / type
 ↓
Fix root cause
 ↓
Avoid unrelated refactoring
 ↓
Verify affected workflow
```

---


# 25. Soft Delete / Logical Delete

The project uses **soft delete (logical delete)** instead of physically removing important records from the database.

## 25.1 Required `is_deleted` field

For entities that need deletion support, add:

```java
@Column(name = "is_deleted", nullable = false)
@Builder.Default
private Boolean isDeleted = false;
```

If the entity does not use Lombok `@Builder`, use:

```java
@Column(name = "is_deleted", nullable = false)
private Boolean isDeleted = false;
```

The database column should be:

```sql
is_deleted BOOLEAN NOT NULL DEFAULT FALSE
```

Meaning:

```text
false -> active / not deleted
true  -> logically deleted
```

Do **not** physically delete the database row.

---

## 25.2 Recommended entities

Add `is_deleted` to persistent business entities where a deleted record should remain in the database.

Recommended:

```text
users
skills
skill_categories
user_skills
learning_goals
connection_requests
learning_sessions
locations
skill_inquiries
user_queries
session_conflicts
```

For `logins`, deletion should normally be represented by the existing account status if that status already supports deactivation/disablement. Do not add another deletion state unless the actual authentication requirements need it.

---

## 25.3 Java field

For each applicable entity, add:

```java
@Column(name = "is_deleted", nullable = false)
@Builder.Default
private Boolean isDeleted = false;
```

Place it near the other state/status fields.

Example:

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private SessionStatus status;

@Column(name = "is_deleted", nullable = false)
@Builder.Default
private Boolean isDeleted = false;
```

If using `@Builder`, `@Builder.Default` is important so a builder-created object also gets `false` when the field is not explicitly supplied.

---

## 25.4 Database migration

For an existing database, do not simply recreate the table.

Add the column with a default value:

```sql
ALTER TABLE users
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE skills
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE skill_categories
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_skills
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE learning_goals
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE connection_requests
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE learning_sessions
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE locations
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE session_conflicts
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE skill_inquiries
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_queries
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
```

Existing rows become:

```text
is_deleted = false
```

---

## 25.5 Soft delete service logic

A delete operation should update the flag:

```java
entity.setIsDeleted(true);
repository.save(entity);
```

Do NOT use:

```java
repository.delete(entity);
```

for entities participating in soft deletion.

A restore operation can use:

```java
entity.setIsDeleted(false);
repository.save(entity);
```

---

## 25.6 Repository query rule

Normal application queries should return only non-deleted records.

Prefer:

```java
List<Skill> findByIsDeletedFalse();
```

or:

```java
Optional<Skill> findByIdAndIsDeletedFalse(Long id);
```

For user-specific data:

```java
List<LearningGoal> findByUserIdAndIsDeletedFalse(Long userId);
```

For sessions:

```java
List<LearningSession> findByLearnerIdAndIsDeletedFalse(Long userId);
```

When a query contains multiple conditions, include the soft-delete condition explicitly:

```java
Optional<ConnectionRequest>
findByIdAndReceiverIdAndIsDeletedFalse(Long id, Long receiverId);
```

The general rule is:

```text
Every normal SELECT must exclude:
is_deleted = true
```

Admin/recovery functionality may intentionally query deleted records.

---

## 25.7 Relationships and soft delete

Soft deletion does not remove foreign-key relationships.

Example:

```text
USER
  │
  └── LEARNING_GOAL
```

If the learning goal is deleted:

```text
learning_goals.is_deleted = true
```

The row remains in the database and still references the user and skill.

This preserves:
- history
- auditability
- relationships
- conflict/session records
- reporting information

Do not set foreign keys to `NULL` merely because a child record is soft-deleted.

---

## 25.8 Deleting a User

User deletion requires special care.

Do not physically delete the user because historical records may reference the user:

```text
ConnectionRequest
LearningSession
SessionConflict
SkillInquiry
UserQuery
LearningGoal
UserSkill
```

Instead:

```java
user.setIsDeleted(true);
```

The application should then prevent the deleted user from:
- logging into normal application flows
- appearing in search results
- appearing in recommendations
- sending new connection requests
- creating new sessions
- sending new inquiries

Existing historical records should remain available where business rules require them.

---

## 25.9 Deleted users and authentication

`users.is_deleted = true` should be checked during authentication/profile access if user deletion is supported.

Do not rely only on:

```text
Login.status
```

unless the project explicitly defines account status as the complete account-deactivation mechanism.

A robust check is conceptually:

```text
Login exists
    AND
Login status allows login
    AND
User exists
    AND
User.is_deleted = false
```

---

## 25.10 Deleted skills

When a skill is soft-deleted:

```text
skills.is_deleted = true
```

It should normally:
- disappear from new skill searches
- not appear in recommendations
- not be selectable for new learning goals
- not be selectable for new sessions
- not be selectable for new connection requests

Historical records referencing the skill remain intact.

This is important because old sessions and learning goals may need to display the original skill.

---

## 25.11 Deleted skill categories

If a category is soft-deleted:

```text
skill_categories.is_deleted = true
```

Normally:
- do not show it in active category lists
- do not allow new skills to be created under it

Existing skills and historical data should not be physically removed automatically.

Before deleting a category, the service should decide whether active skills under that category must be reassigned, deactivated, or handled by a separate business rule.

Do not silently cascade-delete skills.

---

## 25.12 Deleted learning goals and user skills

For:

```text
UserSkill
LearningGoal
```

soft delete means the user's association is removed from the active experience without destroying history.

Example:

```text
UserSkill.is_deleted = true
```

The user no longer appears as actively offering that skill, but historical sessions and records can still reference the skill.

---

## 25.13 Deleted connection requests

A deleted connection request should not appear in normal pending/accepted/rejected request lists.

However, the record remains for historical/audit purposes.

Example:

```java
connectionRequest.setIsDeleted(true);
```

Do not physically delete it.

---

## 25.14 Deleted learning sessions

Be very careful with sessions.

A completed or disputed session should generally **not be physically deleted**.

If a session needs to be removed from the normal active view:

```java
learningSession.setIsDeleted(true);
```

But preserve it for:
- conflict resolution
- history
- reporting
- credit/audit information

Never automatically delete `SessionConflict` merely because the associated session is soft-deleted.

---

## 25.15 Deleted locations

A location can be soft-deleted when it should no longer be selectable for new offline sessions.

Historical sessions should still be able to reference the location record.

Do not physically delete a location that is referenced by historical sessions unless the database/business rules explicitly support that operation.

---

## 25.16 Deleted inquiries and user queries

For:

```text
SkillInquiry
UserQuery
```

soft delete removes them from normal user-facing lists while preserving the record.

This is useful for:
- support history
- moderation
- audit
- administrative investigation

---

## 25.17 API behavior

Normal APIs should hide deleted entities.

For example:

```text
GET /api/skills
```

should return:

```text
is_deleted = false
```

only.

A response should generally not expose internal deletion state unless the frontend/admin UI needs it.

For admin endpoints, deleted records can be included when explicitly requested.

Example conceptual filter:

```text
GET /admin/skills?includeDeleted=true
```

Only implement such an option if required by the application's API design.

---

## 25.18 DTO rule

Do not automatically expose:

```java
private Boolean isDeleted;
```

in every public DTO.

For normal user-facing DTOs, hide it.

For admin DTOs, it may be useful:

```java
private Boolean isDeleted;
```

This keeps the public API clean while allowing administrators to manage deleted records.

---

## 25.19 `@SQLDelete` and `@SQLRestriction`

Hibernate can implement soft deletion automatically with annotations such as:

```java
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
```

However, do not introduce these globally without checking the project's Hibernate version and existing JPA strategy.

For this project, **explicit repository/service filtering is the safer default** unless the project deliberately standardizes on Hibernate-level soft-delete behavior.

If Hibernate-level soft delete is introduced later, apply it consistently and test:
- relationships
- lazy loading
- admin queries
- restore operations
- native SQL queries
- joins

---

## 25.20 Unique constraints and soft delete

This is an important database issue.

Suppose:

```text
skills.name = "Java"
is_deleted = true
```

and the database has:

```text
UNIQUE(name)
```

A new `"Java"` skill still cannot be inserted because the deleted row occupies the unique value.

Therefore, when soft deletion is combined with unique fields, decide the desired behavior.

For MySQL, a common approach is to keep the old row and **restore/reuse it** rather than inserting a duplicate.

For example:

```text
Existing Java skill
        │
        ▼
is_deleted = true
        │
        ▼
Request to create Java
        │
        ▼
Find existing deleted Java
        │
        ▼
Restore it
```

Do not blindly remove unique constraints to solve this.

This applies to fields such as:
- `skills.name`
- `skills.slug`
- `skill_categories.name`
- login/user email fields

The exact strategy should follow the business requirement.

---

# 26. Soft Delete Checklist for AI

Whenever implementing a delete feature, follow:

```text
1. Identify entity
        ↓
2. Check whether it has is_deleted
        ↓
3. Change is_deleted from false → true
        ↓
4. Do not physically delete row
        ↓
5. Update repository queries
        ↓
6. Hide deleted records from normal APIs
        ↓
7. Preserve FK relationships
        ↓
8. Check unique constraints
        ↓
9. Check dependent entities
        ↓
10. Check admin/recovery requirements
```

Before deleting a parent entity, always ask:

```text
What historical records reference this entity?
```

The answer should determine whether deletion is allowed and what the active application should display.

---

# 27. Golden Rule

**Do not treat the project as a collection of independent CRUD tables.**

The platform is a skill-exchange system.

The important business chain is:

```text
USER
  ↓
SKILLS OFFERED + LEARNING GOALS
  ↓
MATCHING / SEARCH
  ↓
CONNECTION REQUEST
  ↓
ACCEPTANCE
  ↓
LEARNING SESSION
  ↓
ONLINE / OFFLINE
  ↓
TWO-WAY SESSION HANDSHAKE
  ↓
SESSION COMPLETION
  ↓
CONFLICT IF SOMETHING GOES WRONG
  ↓
ADMIN RESOLUTION
```

Every implementation should preserve this domain flow and the relationships between these entities.
