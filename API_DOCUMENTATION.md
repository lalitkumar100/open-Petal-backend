# OpenPetal Backend REST API Complete Specification Manual

This document provides complete, exhaustive API documentation for all **75 API endpoints** mapped in the OpenPetal Frontend application ([`API_ROUTES.md`](file:///d:/Open%20Petal/project%20code/open-petal-frontend/API_ROUTES.md)). Each endpoint contains HTTP specs, URL, Authorization, frontend call location, query/path parameters, request JSON payload example, and success response JSON payload example.

---

## Global Standards
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **Header**: `Authorization: Bearer <jwt_token>`

---

## Complete 75 API Endpoints Documentation

### 1. User Login
- **Endpoint**: `POST /api/v1/auth/login`
- **Page**: `/login` | **File**: `src/app/core/services/auth.service.ts`
- **Auth**: None
- **Request Body**:
```json
{
  "email": "keerthi.naik@gmail.com",
  "password": "Password@123"
}
```
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 40,
    "email": "keerthi.naik@gmail.com",
    "fullName": "Keerthi Naik",
    "role": "ROLE_USER",
    "status": "ACTIVE"
  }
}
```

### 2. User Registration
- **Endpoint**: `POST /api/v1/auth/register`
- **Page**: `/register` | **File**: `src/app/core/services/auth.service.ts`
- **Auth**: None
- **Request Body**:
```json
{
  "firstName": "Keerthi",
  "lastName": "Naik",
  "email": "keerthi.naik@gmail.com",
  "password": "Password@123",
  "phone": "+919876543210",
  "dob": "1998-05-15",
  "gender": "FEMALE",
  "description": "Enthusiastic learner & Python teacher"
}
```
- **Response (201 Created)**:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": { "userId": 40, "email": "keerthi.naik@gmail.com" }
}
```

### 3. Forgot Password
- **Endpoint**: `POST /api/v1/auth/forgot-password`
- **Page**: `/forgot-password` | **File**: `src/app/core/services/auth.service.ts`
- **Auth**: None
- **Request Body**:
```json
{ "email": "keerthi.naik@gmail.com" }
```
- **Response (200 OK)**:
```json
{ "success": true, "message": "Password reset link sent to your email", "data": null }
```

### 4. Get System Skills Dropdown List
- **Endpoint**: `GET /api/v1/skills`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "data": [
    { "id": 3, "name": "Python", "description": "Python Programming", "isActive": true },
    { "id": 4, "name": "JavaScript", "description": "JS Development", "isActive": true }
  ]
}
```

### 5. Get Logged-in User Teaching Skills
- **Endpoint**: `GET /api/v1/user/skills`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "User skills retrieved successfully",
  "data": [
    {
      "id": 37,
      "skillId": 3,
      "skillName": "Python",
      "level": "INTERMEDIATE",
      "experienceYears": 1,
      "teachingMode": "OFFLINE",
      "sessionDuration": 60,
      "creditsPerSession": 10,
      "rating": 4.8,
      "peopleTaught": 5,
      "isActive": true,
      "isVerified": true,
      "testMarks": 100
    }
  ]
}
```

### 6. Get Logged-in User Learning Goals
- **Endpoint**: `GET /api/v1/user/learning-goals`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Learning goals retrieved successfully",
  "data": [
    {
      "id": 6,
      "skillName": "Angular",
      "currentLevel": "BEGINNER",
      "targetLevel": "ADVANCED"
    }
  ]
}
```

### 7. Get User Availability Slots
- **Endpoint**: `GET /api/v1/user/availability`
- **Page**: `/user/home` | **File**: `src/app/core/services/user.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Availability slots retrieved",
  "data": [
    { "dayOfWeek": "MONDAY", "startTime": "10:00", "endTime": "12:00" }
  ]
}
```

### 8. Add Teaching Skill
- **Endpoint**: `POST /api/v1/user/skills/teach`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{
  "skillId": 3,
  "skillLevel": "INTERMEDIATE",
  "experienceYears": 2,
  "teachingMode": "ONLINE",
  "creditsPerSession": 10
}
```
- **Response (200 OK)**:
```json
{ "success": true, "message": "Teaching skill added successfully", "data": { "id": 37, "skillName": "Python" } }
```

### 9. Add Learning Goal
- **Endpoint**: `POST /api/v1/user/skills/learn`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{
  "skillId": 6,
  "currentLevel": "BEGINNER",
  "targetLevel": "ADVANCED"
}
```
- **Response (200 OK)**:
```json
{ "success": true, "message": "Learning goal added successfully", "data": { "id": 6 } }
```

### 10. Delete Teaching Skill
- **Endpoint**: `DELETE /api/v1/user/skills/teach/{id}`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{ "success": true, "message": "Teaching skill deleted successfully", "data": null }
```

### 11. Delete Learning Goal
- **Endpoint**: `DELETE /api/v1/user/skills/learn/{id}`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{ "success": true, "message": "Learning goal deleted successfully", "data": null }
```

### 12. Get / Generate AI Verification Questions
- **Endpoint**: `GET /api/v1/user/skills/{skillId}/verify/questions`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Verification questions retrieved successfully",
  "data": "[\n  {\n    \"question\": \"What is output of print(type([])) in Python?\",\n    \"options\": [\"<class 'list'>\", \"<class 'tuple'>\", \"<class 'dict'>\", \"<class 'set'>\"],\n    \"correctAnswer\": \"<class 'list'>\"\n  }\n]"
}
```

### 13. Submit AI Verification Answers
- **Endpoint**: `POST /api/v1/user/skills/{skillId}/verify/submit`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "answers": ["<class 'list'>", "def", "None", "[]", "True"] }
```
- **Response (200 OK)**:
```json
{ "success": true, "message": "Verification assessment submitted successfully", "data": { "status": "PASS", "marks": 100, "isVerified": true } }
```

### 14. Generate AI Learning Roadmap
- **Endpoint**: `POST /api/v1/learning-goals/{goalId}/roadplan/generate`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Roadmap generated successfully",
  "data": { "id": 6, "skillName": "Angular", "roadplan": { "title": "Mastering Angular", "milestones": [] } }
}
```

### 15. Update AI Roadmap Progress
- **Endpoint**: `PATCH /api/v1/learning-goals/{goalId}/roadplan`
- **Page**: `/user/home` | **File**: `src/app/core/services/user-skill.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "milestones": [] }
```

### 16. Fetch Sent Connection Requests
- **Endpoint**: `GET /api/v1/connections/sent`
- **Page**: `/user/home` | **File**: `src/app/pages/user/user-home-page/user-home-page.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{ "success": true, "data": [{ "id": 12, "receiverName": "Alexander Rivera", "status": "PENDING" }] }
```

### 17. Fetch Pending Received Connection Requests
- **Endpoint**: `GET /api/v1/connections/received/pending`
- **Page**: `/user/home` | **File**: `src/app/pages/user/user-home-page/user-home-page.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{ "success": true, "data": [{ "id": 14, "senderName": "Lalit Choudhary", "status": "PENDING" }] }
```

### 18. Accept Connection Request (User Home)
- **Endpoint**: `POST /api/v1/connections/accept/{requestId}`
- **Page**: `/user/home` | **File**: `src/app/pages/user/user-home-page/user-home-page.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{ "success": true, "message": "Connection request accepted successfully", "data": null }
```

### 19. Get Profile Data
- **Endpoint**: `GET /api/v1/user/profile`
- **Page**: `/user/profile`, `/admin/profile` | **File**: `src/app/core/services/user.service.ts`
- **Auth**: Bearer Token

### 20. Update Profile Info
- **Endpoint**: `PUT /api/v1/user/profile`
- **Page**: `/user/profile`, `/admin/profile` | **File**: `src/app/core/services/user.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "firstName": "Keerthi", "lastName": "Naik", "phone": "+919876543210", "description": "Updated Bio" }
```

### 21. Change Password
- **Endpoint**: `PUT /api/v1/user/change-password`
- **Page**: `/user/profile`, `/admin/profile` | **File**: `src/app/core/services/user.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "oldPassword": "OldPassword@123", "newPassword": "NewPassword@123" }
```

### 22. Add Weekly Availability Slot
- **Endpoint**: `POST /api/v1/user/availability`
- **Page**: `/user/profile`, `/admin/profile` | **File**: `src/app/core/services/user.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "dayOfWeek": "TUESDAY", "startTime": "14:00", "endTime": "16:00" }
```

### 23. Remove Weekly Availability Slot
- **Endpoint**: `DELETE /api/v1/user/availability`
- **Page**: `/user/profile`, `/admin/profile` | **File**: `src/app/core/services/user.service.ts`
- **Auth**: Bearer Token

### 24. Search Users & Skills
- **Endpoint**: `GET /api/v1/search/users?searchType=user&query=Python&page=0&size=10`
- **Page**: `/user/search` | **File**: `src/app/pages/user/search-page/search-page.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "userId": 40,
        "firstName": "Keerthi",
        "lastName": "Naik",
        "techSkills": [{ "id": 3, "name": "Python", "isVerified": true }]
      }
    ],
    "totalElements": 1
  }
}
```

### 25. Get Mentor Recommendations
- **Endpoint**: `GET /api/v1/search/recommendations/mentors`
- **Page**: `/user/search` | **File**: `src/app/pages/user/search-page/search-page.ts`
- **Auth**: Bearer Token

### 26. Get Barter Partner Recommendations
- **Endpoint**: `GET /api/v1/search/recommendations/barter`
- **Page**: `/user/search` | **File**: `src/app/pages/user/search-page/search-page.ts`
- **Auth**: Bearer Token

### 27. Get Public User Details
- **Endpoint**: `GET /api/v1/search/users/{userId}/details`
- **Page**: `/user/user-details/:userId` | **File**: `src/app/pages/user/user-details-page/user-details-page.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "userInfo": { "userId": 40, "firstName": "Keerthi", "lastName": "Naik" },
    "skillsOffered": [{ "id": 3, "name": "Python", "level": "INTERMEDIATE", "isVerified": true }],
    "connectionStatus": "NONE"
  }
}
```

### 28. Schedule / Create Session
- **Endpoint**: `POST /api/v1/sessions/create`
- **Page**: `/user/user-details/:userId` | **File**: `src/app/pages/user/user-details-page/user-details-page.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{
  "mentorId": 40,
  "primarySkillId": 3,
  "sessionType": "ONE_TO_ONE",
  "sessionDate": "2026-09-15",
  "startTime": "10:00",
  "endTime": "11:00",
  "mode": "ONLINE",
  "meetingLink": "https://meet.google.com/abc-def"
}
```

### 29. Send Connection Request (User Details Page)
- **Endpoint**: `POST /api/v1/connections/send`
- **Page**: `/user/user-details/:userId` | **File**: `src/app/pages/user/user-details-page/user-details-page.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{
  "receiverId": 40,
  "primarySkillId": 3,
  "offeredSkillId": 4,
  "initialMessage": "Hello!"
}
```

### 30. Accept Connection Request (User Details Page)
- **Endpoint**: `POST /api/v1/connections/accept/{requestId}`
- **Page**: `/user/user-details/:userId` | **File**: `src/app/pages/user/user-details-page/user-details-page.ts`
- **Auth**: Bearer Token

### 31. Paginated System Skills Directory
- **Endpoint**: `GET /api/v1/skills/details/page?page=0&size=10`
- **Page**: `/user/skill` | **File**: `src/app/pages/user/skill-page/skill-page.ts`
- **Auth**: Bearer Token

### 32. Get Logged-in User's Sessions
- **Endpoint**: `GET /api/v1/sessions/my-sessions`
- **Page**: `/user/session` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token

### 33. Get Specific Session Details
- **Endpoint**: `GET /api/v1/sessions/{id}`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token

### 34. Accept Requested Session
- **Endpoint**: `PUT /api/v1/sessions/{id}/accept`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token

### 35. Decline Requested Session
- **Endpoint**: `POST /api/v1/sessions/{id}/decline`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token

### 36. Cancel Active Session
- **Endpoint**: `PUT /api/v1/sessions/{id}/cancel`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token

### 37. Get Session Passwords / OTP
- **Endpoint**: `GET /api/v1/sessions/{id}/my-otp`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token
- **Response (200 OK)**:
```json
{ "success": true, "data": { "startPassword": "4819", "endPassword": "9302" } }
```

### 38. Verify Session Start OTP
- **Endpoint**: `POST /api/v1/sessions/{id}/verify-start`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "otp": "4819" }
```

### 39. Verify Session End OTP
- **Endpoint**: `POST /api/v1/sessions/{id}/verify-end`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "otp": "9302" }
```

### 40. Raise Session Conflict Dispute
- **Endpoint**: `POST /api/v1/sessions/{id}/raise-conflict`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "conflictReason": "MENTOR_NO_SHOW", "story": "Mentor did not join session." }
```

### 41. Get Session Conflict Details
- **Endpoint**: `GET /api/v1/sessions/{sessionId}/conflict`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token

### 42. Submit User Story for Conflict
- **Endpoint**: `PUT /api/v1/sessions/{sessionId}/conflict/story`
- **Page**: `/user/session/:id` | **File**: `src/app/core/services/session.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "story": "Updated explanation statement." }
```

### 43. Get List of User Session Conflicts
- **Endpoint**: `GET /api/v1/conflicts/my-conflicts`
- **Page**: `/user/conflicts` | **File**: `src/app/core/services/conflict.service.ts`
- **Auth**: Bearer Token

### 44. Get User Submitted Support Queries
- **Endpoint**: `GET /queries`
- **Page**: `/user/my-queries` | **File**: `src/app/core/services/query.service.ts`
- **Auth**: Bearer Token

### 45. Submit New Support Query
- **Endpoint**: `POST /queries`
- **Page**: `/user/my-queries` | **File**: `src/app/core/services/query.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "queryType": "TECHNICAL_ISSUE", "subject": "Audio lag", "description": "Audio lag during sessions." }
```

### 46. Get Specific Support Query Details
- **Endpoint**: `GET /queries/{id}`
- **Page**: `/user/my-queries` | **File**: `src/app/core/services/query.service.ts`
- **Auth**: Bearer Token

### 47. Get Active Chat Conversations List
- **Endpoint**: `GET /api/v1/chat/conversations`
- **Page**: `/user/inbox`, Global Panel | **File**: `src/app/core/services/chat.service.ts`
- **Auth**: Bearer Token

### 48. Get Messages for Conversation
- **Endpoint**: `GET /api/v1/chat/{connectionRequestId}/messages`
- **Page**: `/user/inbox`, Global Panel | **File**: `src/app/core/services/chat.service.ts`
- **Auth**: Bearer Token

### 49. Send Direct Chat Message
- **Endpoint**: `POST /api/v1/chat/{connectionRequestId}/send`
- **Page**: `/user/inbox`, Global Panel | **File**: `src/app/core/services/chat.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "content": "Hello! Excited for our session tomorrow." }
```

### 50. Mark Conversation Messages as Read
- **Endpoint**: `PUT /api/v1/chat/{connectionRequestId}/read`
- **Page**: `/user/inbox`, Global Panel | **File**: `src/app/core/services/chat.service.ts`
- **Auth**: Bearer Token

### 51. Get Total Unread Message Count
- **Endpoint**: `GET /api/v1/chat/unread-count`
- **Page**: `/user/inbox`, Global Panel | **File**: `src/app/core/services/chat.service.ts`
- **Auth**: Bearer Token

### 52. Get AI Chat Menu History List
- **Endpoint**: `GET /api/ai/chat-menu`
- **Page**: Floating AI Chat Widget | **File**: `src/app/core/services/ai-chat.service.ts`
- **Auth**: Bearer Token

### 53. Get Specific AI Chat Session History
- **Endpoint**: `GET /api/ai/chat/{chatId}`
- **Page**: Floating AI Chat Widget | **File**: `src/app/core/services/ai-chat.service.ts`
- **Auth**: Bearer Token

### 54. Send Message to AI Assistant (New Chat)
- **Endpoint**: `POST /api/ai/chat`
- **Page**: Floating AI Chat Widget | **File**: `src/app/core/services/ai-chat.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "prompt": "How do list comprehensions work in Python?", "next_gen_summary": "", "new_chat": true }
```

### 55. Send Message to AI Assistant (Existing Chat)
- **Endpoint**: `POST /api/ai/chat/{chatId}`
- **Page**: Floating AI Chat Widget | **File**: `src/app/core/services/ai-chat.service.ts`
- **Auth**: Bearer Token
- **Request Body**:
```json
{ "prompt": "Can you show an example?", "next_gen_summary": "Python list comprehensions", "new_chat": false }
```

### 56. Get Admin Dashboard Summary Statistics
- **Endpoint**: `GET /admin/dashboard`
- **Page**: `/admin/home` | **File**: `src/app/core/services/admin-dashboard.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)
- **Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "totalUsers": 120,
    "newUsers24h": 5,
    "totalSkills": 45,
    "totalSkillCategories": 8,
    "totalConflicts": 2,
    "totalSessions": 340,
    "sessions24h": 12,
    "newQueries24h": 3
  }
}
```

### 57. Admin Paginated Search Users
- **Endpoint**: `GET /api/v1/admin/user?searchType=name&searchQuery=Keerthi&page=0&size=10`
- **Page**: `/admin/user` | **File**: `src/app/pages/admin/user-page/user-page.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 58. Admin View User Details
- **Endpoint**: `GET /api/v1/search/users/{userId}/details`
- **Page**: `/admin/users/:userId/details` | **File**: `src/app/pages/admin/user-details-page/user-details-page.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 59. Get Skill Categories List
- **Endpoint**: `GET /api/v1/skill-categories`
- **Page**: `/admin/skill-categories` | **File**: `src/app/pages/admin/skill-category-page/skill-category-page.ts`
- **Auth**: Bearer Token

### 60. Create New Skill Category
- **Endpoint**: `POST /api/v1/admin/skill-categories`
- **Page**: `/admin/skill-categories` | **File**: `src/app/components/add-skill-category-dialog/add-skill-category-dialog.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)
- **Request Body**:
```json
{ "name": "Programming", "description": "Software & Web Development" }
```

### 61. Get Category Details
- **Endpoint**: `GET /api/v1/admin/skill-categories/{categoryId}`
- **Page**: `/admin/skill-categories/:id/details` | **File**: `src/app/pages/admin/skill-category-details-page/skill-category-details-page.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 62. Get Skills Under Category
- **Endpoint**: `GET /api/v1/admin/skill-categories/{categoryId}/skills`
- **Page**: `/admin/skill-categories/:id/details` | **File**: `src/app/pages/admin/skill-category-details-page/skill-category-details-page.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 63. Add New Skill Under Category
- **Endpoint**: `POST /api/v1/admin/skills`
- **Page**: `/admin/skill-categories/:id/details` | **File**: `src/app/components/add-skill-dialog/add-skill-dialog.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)
- **Request Body**:
```json
{ "categoryId": 1, "name": "TypeScript", "description": "Typed JavaScript" }
```

### 64. Get Skill Details
- **Endpoint**: `GET /api/v1/admin/skills/{skillId}/details`
- **Page**: `/admin/skill-categories/:id/details` | **File**: `src/app/pages/admin/skill-category-details-page/skill-category-details-page.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 65. Admin Get All Sessions
- **Endpoint**: `GET /api/v1/admin/sessions`
- **Page**: `/admin/session` | **File**: `src/app/core/services/admin-session.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 66. Admin Get Session Details
- **Endpoint**: `GET /api/v1/admin/sessions/{id}`
- **Page**: `/admin/session` | **File**: `src/app/core/services/admin-session.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 67. Admin Force Cancel Session
- **Endpoint**: `PUT /api/v1/admin/sessions/{id}/cancel`
- **Page**: `/admin/session` | **File**: `src/app/core/services/admin-session.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)
- **Request Body**:
```json
{ "reason": "Violation of community guidelines." }
```

### 68. Admin Get All Session Conflicts
- **Endpoint**: `GET /api/v1/admin/conflicts`
- **Page**: `/admin/conflicts`, `/admin/queries` | **File**: `src/app/core/services/admin-conflict.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 69. Admin Get Conflict Details
- **Endpoint**: `GET /api/v1/admin/conflicts/{id}`
- **Page**: `/admin/conflicts/:id/details` | **File**: `src/app/core/services/admin-conflict.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 70. Admin Resolve Conflict Dispute
- **Endpoint**: `PUT /api/v1/admin/conflicts/{id}/resolve`
- **Page**: `/admin/conflicts/:id/details` | **File**: `src/app/core/services/admin-conflict.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)
- **Request Body**:
```json
{ "resolutionAction": "REFUND_LEARNER", "adminNotes": "Learner proved mentor did not attend." }
```

### 71. Admin Get All Support Queries
- **Endpoint**: `GET /admin/queries`
- **Page**: `/admin/queries` | **File**: `src/app/core/services/admin-query.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)

### 72. Admin Reply To Support Query
- **Endpoint**: `PATCH /admin/queries/{id}/reply`
- **Page**: `/admin/queries` | **File**: `src/app/core/services/admin-query.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)
- **Request Body**:
```json
{ "status": "RESOLVED", "adminResponse": "We have resolved the audio lag issue." }
```

### 73. Get All Offline Locations
- **Endpoint**: `GET /api/admin/locations`
- **Page**: `/admin/locations` | **File**: `src/app/core/services/location.service.ts`
- **Auth**: Bearer Token

### 74. Get Location Details By ID
- **Endpoint**: `GET /api/admin/locations/{id}`
- **Page**: `/admin/locations` | **File**: `src/app/core/services/location.service.ts`
- **Auth**: Bearer Token

### 75. Create New Offline Location
- **Endpoint**: `POST /api/admin/locations`
- **Page**: `/admin/locations` | **File**: `src/app/core/services/location.service.ts`
- **Auth**: Bearer Token (Role: `ROLE_ADMIN`)
- **Request Body**:
```json
{
  "addressLine1": "OpenPetal Tech Hub, Block B",
  "addressLine2": "Outer Ring Road",
  "area": "Marathahalli",
  "city": "Bengaluru",
  "pincode": "560037"
}
```
- **Response (200 OK)**:
```json
{
  "id": 1,
  "addressLine1": "OpenPetal Tech Hub, Block B",
  "addressLine2": "Outer Ring Road",
  "area": "Marathahalli",
  "city": "Bengaluru",
  "pincode": "560037"
}
```
