# OpenPetal Backend Unit & Integration Test Rationale Manual

This document explains the technical reasons and business rationale behind every unit test, integration test, and Mockito test suite in the OpenPetal Backend repository (`src/test/java/com/crimsonlogic/open_petal_backend/`).

---

## Testing Architecture & Tools
- **Test Framework**: JUnit 5 (Jupiter) & AssertJ (`assertThat`)
- **Mocking Library**: Mockito (`@Mock`, `@InjectMocks`, `given()`, `verify()`)
- **API Test Suite**: Spring MockMvc (`MockMvcBuilders`, `status()`, `jsonPath()`)
- **Validation**: Jakarta Bean Validation (`ValidatorFactory`, `@NotBlank`, `@Email`)

---

## 1. Entity Test Cases Rationale (`src/test/java/com/crimsonlogic/open_petal_backend/entity/`)

### 1.1 `UserTest.java`
- **Reason for Test Case**:
  - **Age Validation (`isOfValidAge()`)**: Ensures users registering on OpenPetal are at least 18 years old to comply with legal terms of service for peer-to-peer skill barter and credit transactions.
  - **Null DOB Handling**: Verifies that missing date-of-birth values return `false` gracefully without crashing the application with a `NullPointerException`.
  - **Full Name Formatting**: Tests the helper method `getFullName()` to ensure clean space-separated string concatenation of first and last names across UI displays.

### 1.2 `LoginTest.java`
- **Reason for Test Case**:
  - **Password Encryption Contract**: Validates that raw passwords are properly hashed before saving to ensure credentials can never be compromised in plain text.
  - **Account Status State Machine**: Verifies that user account statuses (`ACTIVE`, `SUSPENDED`, `INACTIVE`) change correctly when administrative actions occur.
  - **Role Type Enforcement**: Tests role assignment (`ROLE_USER`, `ROLE_ADMIN`) to prevent unauthorized privilege escalation.

### 1.3 `UserSkillTest.java`
- **Reason for Test Case**:
  - **Skill Offering Specs**: Validates user-defined skill parameters (teaching mode, experience years, session duration, and required credit rate).
  - **Verification Status Flag**: Ensures that `isVerified` defaults to `false` until the user successfully completes the AI MCQ assessment.

### 1.4 `SkillTest.java`
- **Reason for Test Case**:
  - **Automated Slug Generation**: Tests that skill names (e.g. "Data Science") automatically generate clean URL-friendly slugs (e.g. `data-science`) for SEO routing.
  - **Active Status Toggle**: Verifies that skills can be enabled or disabled by administrators without breaking user skill references.

### 1.5 `SkillCategoryTest.java`
- **Reason for Test Case**:
  - **Category-to-Skill Association**: Ensures 1-to-many relationship mapping between category entities and individual system skills remains intact.

### 1.6 `LearningGoalTest.java`
- **Reason for Test Case**:
  - **Goal Progress Tracking**: Validates target skill levels (`BEGINNER` $\rightarrow$ `EXPERT`) and confirms that AI-generated roadmap JSON strings bind cleanly to goals.

### 1.7 `LearningSessionTest.java`
- **Reason for Test Case**:
  - **Session Password / OTP Verification**: Tests that 4-digit start and end verification passwords generated for sessions match expected security criteria.
  - **Credit Holding Mechanics**: Verifies that credit points are locked during booking and only transferred upon successful session end OTP verification.

### 1.8 `SessionFeedbackTest.java`
- **Reason for Test Case**:
  - **Star Rating Boundary Enforcement**: Ensures star ratings strictly remain between $1$ (minimum) and $5$ (maximum) stars to protect rating calculations.

### 1.9 `SessionConflictTest.java`
- **Reason for Test Case**:
  - **Dispute Resolution Tracking**: Validates conflict state transitions (`OPEN`, `RESOLVED_REFUND`, `RESOLVED_RELEASE`) and preserves learner and mentor statements for admin review.

### 1.10 `ConnectionRequestTest.java`
- **Reason for Test Case**:
  - **Peer Barter Matching**: Tests initial barter message storage and primary/offered skill pairs during peer-to-peer connection requests.

### 1.11 `ChatMessageTest.java`
- **Reason for Test Case**:
  - **Direct Message Delivery Status**: Validates status tracking (`SENT`, `DELIVERED`, `READ`) for real-time peer messaging between connected users.

### 1.12 `SkillInquiryTest.java`
- **Reason for Test Case**:
  - **Pre-Booking Skill Inquiries**: Tests inquiry messaging between prospective learners and mentors prior to scheduling formal sessions.

### 1.13 `UserQueryTest.java`
- **Reason for Test Case**:
  - **Support Desk Processing**: Validates query types (`TECHNICAL_ISSUE`, `BILLING`, `GENERAL_INQUIRY`) and priority assignments for support tickets.

### 1.14 `LocationTest.java`
- **Reason for Test Case**:
  - **Offline Session Location Integrity**: Verifies physical address formatting (address line 1, area, city, pincode) for face-to-face mentorship.

### 1.15 `AiChatTest.java`
- **Reason for Test Case**:
  - **AI Assistant Session State**: Tests UUID primary key generation, title summary, and last message timestamp updates for AI conversations.

### 1.16 `AiChatMessageTest.java`
- **Reason for Test Case**:
  - **AI Role & Payload Storage**: Validates message role (`user`, `model`) and JSON payload mapping for AI assistant chat histories.

---

## 2. Service Layer Test Cases Rationale (`src/test/java/com/crimsonlogic/open_petal_backend/service/impl/`)

### 2.1 `AuthServiceImplTest.java`
- **Reason for Test Case**:
  - **Registration Transaction Flow**: Guarantees atomic creation of both `Login` and `User` entities upon successful registration.
  - **Duplicate Registration Prevention**: Verifies that registering with an already existing email throws a `409 CONFLICT` exception immediately.
  - **Verification Email Dispatch**: Asserts that `EmailService` is invoked to send a verification token upon successful registration.

### 2.2 `UserServiceImplTest.java`
- **Reason for Test Case**:
  - **Profile Isolation & Security**: Ensures users can only fetch and update authorized profile fields.
  - **Availability Slot Management**: Tests adding and removing weekly time slots without corrupting existing schedules.

### 2.3 `SkillServiceImplTest.java`
- **Reason for Test Case**:
  - **System Skill Directory Lookup**: Verifies paginated retrieval of skills and skill category lookup accuracy.

### 2.4 `AdminDashboardServiceImplTest.java`
- **Reason for Test Case**:
  - **Dashboard Metrics Aggregation**: Validates accurate calculation of platform metrics (total users, new 24h registrations, active sessions, open conflicts).

### 2.5 `AdminConflictServiceImplTest.java`
- **Reason for Test Case**:
  - **Financial Credit Dispute Resolution**: Ensures that resolving a conflict in favor of the learner refunds held credits back to the learner's account, whereas resolving in favor of the mentor releases credits to the mentor.

### 2.6 `ConflictServiceImplTest.java`
- **Reason for Test Case**:
  - **User Dispute History**: Verifies that users can view their active session conflicts and submit personal statements.

### 2.7 `LearningGoalServiceImplTest.java`
- **Reason for Test Case**:
  - **Goal Creation & Duplicate Protection**: Tests learning goal creation, prevents active duplicate goals for the same skill (`409 CONFLICT`), and validates error handling for missing users or skills.

### 2.8 `SearchServiceImplTest.java`
- **Reason for Test Case**:
  - **User Search & Skill Badge Mapping**: Verifies public user detail lookups for search previews and throws `RecordNotFoundException` when user IDs do not exist.

### 2.9 `SkillCategoryServiceImplTest.java`
- **Reason for Test Case**:
  - **Category Uniqueness & Pagination**: Tests skill category creation, enforces case-insensitive name uniqueness (`IllegalArgumentException`), and validates paginated category searches.

### 2.10 `UserQueryServiceImplTest.java`
- **Reason for Test Case**:
  - **Support Query Lifecycle**: Tests support query submission with explicit `PENDING` initial status, query fetching by user, and admin reply processing.

### 2.11 `UserSkillServiceImplTest.java`
- **Reason for Test Case**:
  - **Teaching Skill Profile Management**: Tests adding new teaching skills to a user's profile, prevents duplicate skill listings, and validates error handling when fetching skill details for verification.

### 2.12 `SessionServiceImplTest.java`
- **Reason for Test Case**:
  - **Session Lifecycle & Credit Holding**: Tests session retrieval by ID, status verification (`REQUESTED`, `ACCEPTED`), 4-digit OTP start/end verification, and credit holding rules.

### 2.13 `EmailServiceTest.java`
- **Reason for Test Case**:
  - **Mail Sender Dispatch**: Verifies that verification emails are dispatched via Spring `JavaMailSender` with correct recipient and token parameters.

### 2.14 `ChatServiceImplTest.java`
- **Reason for Test Case**:
  - **Conversation Summaries & Unread Counts**: Verifies fetching active chat conversation summaries for a user, formatting partner initials (e.g. "Bob Jones" $\rightarrow$ "BJ"), and counting unread messages per connection.
  - **Participant Authorization**: Ensures non-participants are blocked from reading conversation history (`AuthorizationException`).
  - **Direct Message Delivery**: Validates messaging restrictions in non-active connection states (`PENDING`) and confirms saving and returning new chat messages when the connection is `ACCEPTED`.
  - **Batch Read Status Updates**: Asserts that `markAsRead` updates message status to `READ` for the receiver.

### 2.15 `ConnectionRequestServiceImplTest.java`
- **Reason for Test Case**:
  - **Barter Peer Connection Lifecycle**: Validates sending peer connection requests, initial chat message creation, duplicate request prevention, and authorization checks during request acceptance.
  - **Automatic Conversation Initialization**: Confirms auto-generation of initial greeting messages upon connection acceptance if no previous chat history exists.
  - **Sent & Received Request Queries**: Validates user retrieval of sent and pending received connection requests formatted into DTOs with initials.

### 2.16 `LocationServiceImplTest.java`
- **Reason for Test Case**:
  - **Location CRUD Operations**: Tests creating physical locations, mapping details to DTO responses, and soft-delete filtering (`isDeleted = false`).
  - **Error Handling**: Confirms throwing `ResourceNotFoundException` when attempting to fetch non-existent or soft-deleted location records by ID.

### 2.17 `SessionCronServiceTest.java`
- **Reason for Test Case**:
  - **Automated Scheduled Session Expirations**: Verifies scheduled cron job behavior that auto-cancels `SCHEDULED` learning sessions that exceed their 15-minute start window.
  - **Automated Learner Credit Refunds**: Confirms that cancelled credit-based sessions automatically refund held credits back to the learner.
  - **Automated Mentor Credit Releases**: Tests auto-completion of `STARTED` sessions past their end time plus 15 minutes, transferring held credits directly to the mentor.

### 2.18 `GeminiAiClientImplTest.java`
- **Reason for Test Case**:
  - **LLM Content Generation Parsing**: Validates extracting JSON response strings from Gemini API candidate structures.
  - **Empty Candidate Handling**: Ensures runtime exception handling when Gemini returns no candidates (e.g., safety filters or quota limits).

### 2.19 `AiChatServiceTest.java`
- **Reason for Test Case**:
  - **AI Conversation & Query Pipeline**: Tests end-to-end processing of user prompts with the Gemini client, executing read-only SQL queries via `JdbcTemplate`, and persisting conversation history (`AiChat` and `AiChatMessage`).
  - **SQL Security & Sanitization**: Validates that destructive or modifying SQL queries (e.g., `DROP TABLE`, `DELETE`, `UPDATE`) returned by AI models are strictly intercepted and blocked before execution.

---

---

## 3. Controller Layer Test Cases Rationale (`src/test/java/com/crimsonlogic/open_petal_backend/controller/`)

Every controller test suite includes `@BeforeAll` and `@AfterAll` static lifecycle methods to log, set up, and clean up test environment resources, alongside MockMvc standalone controller testing.

### 3.1 `AdminConflictControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes conflict resolution test resources; `@AfterAll` tears down test state.
- **Reason for Test Case**:
  - **Open Conflicts Listing**: Validates `GET /api/v1/admin/conflicts/open` returns 200 OK with pending conflict summaries.
  - **Conflict Details Retrieval**: Tests `GET /api/v1/admin/conflicts/{conflictId}` for full dispute statement payload.
  - **Conflict Resolution Decision**: Tests `POST /api/v1/admin/conflicts/{conflictId}/resolve` processing resolution actions (`REFUND_LEARNER`, `PAYOUT_MENTOR`).

### 3.2 `AdminControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs admin management suite initialization; `@AfterAll` cleans up context.
- **Reason for Test Case**:
  - **Dashboard Metrics Endpoint**: Validates `GET /api/v1/admin/dashboard` returning aggregate platform stats.
  - **User Overview List**: Tests `GET /api/v1/admin/users` returning user accounts overview.
  - **Account Status Modification**: Tests `PUT /api/v1/admin/users/{userId}/status` updating status to `SUSPENDED` or `ACTIVE`.

### 3.3 `AdminLocationControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs location admin test setup; `@AfterAll` logs suite teardown.
- **Reason for Test Case**:
  - **Location Creation API**: Tests `POST /api/v1/admin/locations` creating new offline session venues (201 Created).
  - **Location Details by ID**: Tests `GET /api/v1/admin/locations/{id}` returning specific location entity.
  - **All Locations List**: Tests `GET /api/v1/admin/locations` returning active location summaries.

### 3.4 `AdminSessionControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes global session test state; `@AfterAll` completes suite cleanup.
- **Reason for Test Case**:
  - **Global Sessions Directory**: Tests `GET /api/v1/admin/sessions` returning all platform session summaries.
  - **Admin Session Details**: Tests `GET /api/v1/admin/sessions/{sessionId}` returning detailed session OTPs and links.
  - **Administrative Session Cancellation**: Tests `POST /api/v1/admin/sessions/{sessionId}/cancel` with cancellation reason.

### 3.5 `SkillCategoryControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs skill category suite start; `@AfterAll` logs suite completion.
- **Reason for Test Case**:
  - **Category Creation**: Tests `POST /api/v1/skill-categories` returning 201 Created with saved entity.
  - **Payload Validation**: Confirms blank category names trigger HTTP 400 Bad Request.
  - **Paginated & ID Lookups**: Tests `GET /api/v1/skill-categories` and `GET /api/v1/skill-categories/{id}` endpoints.

### 3.6 `SkillControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes skill directory test suite; `@AfterAll` logs teardown.
- **Reason for Test Case**:
  - **System Skills Directory**: Tests `GET /api/v1/skills` returning full system skills list.
  - **Paginated Skill Details**: Tests `GET /api/v1/skills/details/page` with search query parameter.
  - **Non-Matching Search Handling**: Verifies empty content page returned when no skills match the search query.

### 3.7 `AiChatControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes AI assistant endpoint test context; `@AfterAll` logs cleanup.
- **Reason for Test Case**:
  - **AI Prompt Execution**: Tests `POST /api/v1/ai/chat` sending prompts and headers (`X-User-Id`, `X-User-Role`, `X-User-Name`).
  - **AI Menu Options**: Tests `GET /api/v1/ai/menu` returning structured prompt templates.
  - **AI Conversation History**: Tests `GET /api/v1/ai/history` returning past AI conversation summaries.

### 3.8 `AuthControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes authentication suite; `@AfterAll` logs suite completion.
- **Reason for Test Case**:
  - **User Registration API**: Tests `POST /api/v1/auth/register` returning 201 Created and user DTO.
  - **Validation Interception**: Verifies malformed email payloads return HTTP 400 Bad Request.
  - **User Login Endpoint**: Tests `POST /api/v1/auth/login` returning HTTP 200 OK with JWT authentication token.
  - **Empty Password Protection**: Ensures empty passwords return HTTP 400 Bad Request without calling service logic.

### 3.9 `ChatControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs direct messaging suite start; `@AfterAll` logs teardown.
- **Reason for Test Case**:
  - **User Conversations List**: Tests `GET /api/v1/chat/conversations` returning active conversation previews.
  - **Message History Retrieval**: Tests `GET /api/v1/chat/conversations/{connectionRequestId}/messages` returning chat history.
  - **Send Direct Message**: Tests `POST /api/v1/chat/conversations/{connectionRequestId}/messages` returning 201 Created.

### 3.10 `ConflictControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs session conflict test setup; `@AfterAll` logs suite completion.
- **Reason for Test Case**:
  - **Raise Conflict Request**: Tests `POST /api/v1/conflicts/{sessionId}/raise` submitting conflict reason enum (`NO_SHOW`).
  - **Session Conflict Details**: Tests `GET /api/v1/conflicts/{sessionId}` returning conflict status and statements.
  - **User Statement Submission**: Tests `POST /api/v1/conflicts/{sessionId}/statement` submitting user narrative.

### 3.11 `ConnectionRequestControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes connection request test context; `@AfterAll` logs teardown.
- **Reason for Test Case**:
  - **Send Connection Request**: Tests `POST /api/v1/connection-requests` returning 201 Created.
  - **Accept Request**: Tests `POST /api/v1/connection-requests/{requestId}/accept` updating status to `ACCEPTED`.
  - **Sent Requests Query**: Tests `GET /api/v1/connection-requests/sent` returning user's sent request DTOs.

### 3.12 `LearningGoalControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs learning goal suite initialization; `@AfterAll` logs completion.
- **Reason for Test Case**:
  - **Create Goal Endpoint**: Tests `POST /api/v1/learning-goals` returning 200 OK with saved goal.
  - **Goal Lookup by ID**: Tests `GET /api/v1/learning-goals/{id}` returning specific goal details.
  - **Delete Goal Endpoint**: Tests `DELETE /api/v1/learning-goals/{id}` soft-deleting learning goal.

### 3.13 `SearchControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes search endpoint test environment; `@AfterAll` logs teardown.
- **Reason for Test Case**:
  - **User Search Query**: Tests `GET /api/v1/search/users` with keyword filter returning paginated summaries.
  - **User Details Preview**: Tests `GET /api/v1/search/users/{userId}` returning full public profile DTO.
  - **Mentor Recommendations**: Tests `GET /api/v1/search/mentors` returning suggested mentors list.

### 3.14 `SessionControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs learning session suite setup; `@AfterAll` logs teardown.
- **Reason for Test Case**:
  - **Create Session Request**: Tests `POST /api/v1/sessions/create` scheduling a new session (200 OK).
  - **User Sessions List**: Tests `GET /api/v1/sessions/my-sessions` returning user's session summaries.
  - **Session Details**: Tests `GET /api/v1/sessions/{sessionId}` returning detailed session response.

### 3.15 `UserControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` initializes user management controller tests; `@AfterAll` logs suite completion.
- **Reason for Test Case**:
  - **User Profile Retrieval**: Tests `GET /api/v1/users/profile` returning authenticated user profile.
  - **Active Users Directory**: Tests `GET /api/v1/users/active` returning list of active users.
  - **User Lookup by ID**: Tests `GET /api/v1/users/{id}` returning user entity details.

### 3.16 `UserQueryControllerTest.java`
- **Lifecycle Setup**: `@BeforeAll` logs support query test suite start; `@AfterAll` logs teardown.
- **Reason for Test Case**:
  - **Submit Support Query**: Tests `POST /api/v1/queries` submitting query ticket with `QueryType` enum.
  - **User Submitted Queries**: Tests `GET /api/v1/queries/my` returning user's query history.
  - **Admin Query Reply**: Tests `POST /api/v1/queries/{queryId}/reply` updating status to `RESOLVED` with response.

---

## 4. DTO Validation Test Cases Rationale (`src/test/java/com/crimsonlogic/open_petal_backend/dto/`)

### 4.1 `LoginRequestDtoTest.java`
- **Reason for Test Case**:
  - **Input Sanitization**: Tests `@NotBlank` and `@Email` annotations to reject malformed or blank login credentials before reaching service logic.

### 4.2 `RegisterRequestDtoTest.java`
- **Reason for Test Case**:
  - **Registration Input Rules**: Validates field constraints (email format, password complexity, required names) to prevent invalid database records.

### 4.3 `AddSkillRequestDtoTest.java`
- **Reason for Test Case**:
  - **Skill Creation Validation**: Validates `@NotNull` category IDs and `@NotBlank` skill names when creating new system skills.
