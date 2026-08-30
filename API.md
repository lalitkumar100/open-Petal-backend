# Open Petal Backend API Documentation

## 1. Authentication Module (`/api/v1/auth`)

### 1.1. Register User
- **URL**: `POST /api/v1/auth/register`
- **Description**: Registers a new user and sends an email verification link.
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "StrongPassword123!",
    "firstName": "John",
    "lastName": "Doe",
    "dob": "1990-01-01"
  }
  ```
- **Response**: `201 Created` with user details (excluding password). Status is initially `BLOCKED` (inactive) until verified.

### 1.2. Verify Email
- **URL**: `GET /api/v1/auth/verify?token={token}`
- **Description**: Verifies a user's email using the token sent during registration, changing status to `ACTIVE`.
- **Response**: `200 OK`

### 1.3. Login
- **URL**: `POST /api/v1/auth/login`
- **Description**: Authenticates user and returns a JWT token. Fails if user is `BLOCKED` or `INACTIVE`.
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "StrongPassword123!"
  }
  ```
- **Response**: `200 OK` with JWT token, token expiration, and user details.

### 1.4. Forgot Password
- **URL**: `POST /api/v1/auth/forgot-password`
- **Description**: Sends a password reset email (valid for 15 minutes) if the account exists.
- **Request Body**:
  ```json
  {
    "email": "user@example.com"
  }
  ```
- **Response**: `200 OK`

### 1.5. Reset Password
- **URL**: `POST /api/v1/auth/reset-password?token={token}`
- **Description**: Resets the password using a valid reset token.
- **Request Body**:
  ```json
  {
    "newPassword": "NewStrongPassword123!"
  }
  ```
- **Response**: `200 OK`

---

## 2. User Module (`/api/v1/user`)
*All routes require a valid JWT token (`Authorization: Bearer <token>`) with `ROLE_USER` or `ROLE_ADMIN`.*

### 2.1. Get User Profile
- **URL**: `GET /api/v1/user/profile`
- **Description**: Retrieves the authenticated user's profile details.
- **Response**: `200 OK` containing `UserProfileDto`.

### 2.2. Change Password
- **URL**: `PUT /api/v1/user/change-password`
- **Description**: Changes the authenticated user's password. Immediately invalidates older tokens across devices.
- **Request Body**:
  ```json
  {
    "currentPassword": "StrongPassword123!",
    "newPassword": "NewStrongPassword123!"
  }
  ```
- **Response**: `200 OK`

### 2.3. Edit Profile
- **URL**: `PUT /api/v1/user/profile`
- **Description**: Updates personal details in the `users` table. Note: Email cannot be updated through this route.
- **Request Body**:
  ```json
  {
    "firstName": "Alexander",
    "lastName": "Rivera",
    "phone": "+919876543210",
    "dob": "1999-08-20",
    "gender": "MALE"
  }
  ```
- **Response**: `200 OK` containing the updated `UpdateProfileResponseDto`.

### 2.4. User Self Status Toggle
- **URL**: `PATCH /api/v1/user/status`
- **Description**: Allows a user to temporarily set their account to `INACTIVE` or switch back to `ACTIVE`.
- **Request Body**:
  ```json
  {
    "status": "INACTIVE"
  }
  ```
- **Response**: `200 OK` containing `UpdateStatusResponseDto`.
- **Errors**: `400 Bad Request` if attempting to set status to `BLOCKED`.

---

## 3. Admin Module (`/api/v1/admin`)
*All routes require a valid JWT token (`Authorization: Bearer <token>`) with strictly `ROLE_ADMIN`.*

### 3.1. Admin Dashboard
- **URL**: `GET /api/v1/admin/dashboard`
- **Description**: A test route to verify admin access.
- **Response**: `200 OK`

### 3.2. Get All Users
- **URL**: `GET /api/v1/admin/users`
- **Description**: Retrieves a list of all users in the system.
- **Response**: `200 OK` containing a list of `UserProfileDto`.

### 3.3. Get User by ID
- **URL**: `GET /api/v1/admin/users/{id}`
- **Description**: Retrieves a specific user's profile.
- **Response**: `200 OK` containing `UserProfileDto`.
- **Errors**: `404 Not Found` if the user ID does not exist.

### 3.4. Block User
- **URL**: `PUT /api/v1/admin/users/{id}/block`
- **Description**: Blocks a user, changing their status to `BLOCKED`. This instantly invalidates all their active JWT tokens globally.
- **Response**: `200 OK`
- **Errors**: `404 Not Found` if the user ID does not exist.

### 3.5. Unblock User
- **URL**: `PUT /api/v1/admin/users/{id}/unblock`
- **Description**: Unblocks a user, restoring their status to `ACTIVE`.
- **Response**: `200 OK`
- **Errors**: `404 Not Found` if the user ID does not exist.