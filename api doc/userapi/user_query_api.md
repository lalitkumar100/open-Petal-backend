# User API: Submit Queries

This document outlines the API endpoints for users to submit queries, such as requesting a new skill, reporting an issue, or seeking general help.

## Base URL
`/api/v1/queries`

## Endpoints

### 1. Submit a New Query
Submits a query or support ticket to the administrators.

- **URL:** `/`
- **Method:** `POST`
- **Headers:** 
  - `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "userId": 5,
    "queryType": "ADD_NEW_SKILL",
    "subject": "Add Python Skill",
    "description": "Please add Python to the master skill list as I'd like to teach it."
  }
  ```
  *(Note: `queryType` can be `ADD_NEW_SKILL`, `GENERAL_INQUIRY`, `BUG_REPORT`, or `OTHER`)*
- **Success Response:**
  - **Code:** 200 OK
  - **Content:**
    ```json
    {
      "success": true,
      "message": "Query submitted successfully",
      "data": {
        "id": 1,
        "queryType": "ADD_NEW_SKILL",
        "status": "PENDING"
      }
    }
    ```

### 2. Get User Queries
Retrieves all queries submitted by a specific user.

- **URL:** `/user/{userId}`
- **Method:** `GET`
- **Success Response:**
  - **Code:** 200 OK
  - **Content:**
    ```json
    {
      "success": true,
      "message": "User queries retrieved successfully",
      "data": [
        {
          "id": 1,
          "queryType": "ADD_NEW_SKILL",
          "status": "RESOLVED",
          "subject": "Add Python Skill",
          "description": "Please add Python.",
          "adminResponse": "Added.",
          "createdAt": "2026-09-01T10:00:00Z"
        }
      ]
    }
    ```
