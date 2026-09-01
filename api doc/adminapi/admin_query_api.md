# Admin API: Manage User Queries

This document outlines the API endpoints for administrators to view and respond to user queries (such as requests to add new skills, report bugs, etc.).

## Base URL
`/api/v1/admin/queries`

## Endpoints

### 1. Get All Queries
Retrieves a list of all user queries. You can optionally filter them by their status.

- **URL:** `/`
- **Method:** `GET`
- **Headers:** 
  - `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (string, optional): Filter by query status (e.g., `PENDING`, `IN_REVIEW`, `RESOLVED`, `REJECTED`).
- **Success Response:**
  - **Code:** 200 OK
  - **Content:**
    ```json
    {
      "success": true,
      "message": "Queries retrieved successfully",
      "data": [
        {
          "id": 1,
          "queryType": "ADD_NEW_SKILL",
          "status": "PENDING",
          "subject": "Add Python Skill",
          "description": "Please add Python to the master skill list.",
          "adminResponse": null,
          "createdAt": "2026-09-01T10:00:00Z"
        }
      ]
    }
    ```

### 2. Admin Reply to Query
Allows an admin to reply to a user query and update its status.

- **URL:** `/{queryId}/reply`
- **Method:** `PATCH`
- **Headers:** 
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- **Request Body:**
  ```json
  {
    "status": "RESOLVED",
    "adminResponse": "Python has been successfully added to the catalog."
  }
  ```
- **Success Response:**
  - **Code:** 200 OK
  - **Content:**
    ```json
    {
      "success": true,
      "message": "Query replied successfully",
      "data": {
        "id": 1,
        "status": "RESOLVED",
        "adminResponse": "Python has been successfully added to the catalog."
      }
    }
    ```
