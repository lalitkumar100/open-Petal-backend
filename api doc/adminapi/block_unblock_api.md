# Admin API: Block and Unblock Users

This document outlines the API endpoints for blocking and unblocking users. When blocking or unblocking a user, an optional reason can be provided to keep track of administrative actions.

## Base URL
`/api/v1/admin/users`

## Endpoints

### 1. Block User
Blocks a specific user account. The reason for blocking will be stored in the database.

- **URL:** `/{id}/block`
- **Method:** `PATCH`
- **Headers:** 
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "reason": "Violation of terms of service"
  }
  ```
- **Success Response:**
  - **Code:** 200 OK
  - **Content:**
    ```json
    {
      "success": true,
      "message": "User blocked successfully",
      "data": null
    }
    ```

### 2. Unblock User
Unblocks a specific user account. The reason for unblocking will be stored in the database.

- **URL:** `/{id}/unblock`
- **Method:** `PATCH`
- **Headers:** 
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "reason": "Issue resolved"
  }
  ```
- **Success Response:**
  - **Code:** 200 OK
  - **Content:**
    ```json
    {
      "success": true,
      "message": "User unblocked successfully",
      "data": null
    }
    ```

## Database Changes
The `Login` entity has been updated to track the block/unblock reason. 
- `reason` (VARCHAR): Stores the reason provided during the block or unblock action.
- `updated_at` (TIMESTAMP): Automatically captures the exact time the block or unblock action was performed.
