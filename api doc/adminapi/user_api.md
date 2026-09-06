# Admin User Management API

## Endpoint: Get Users (Paginated & Searchable)

Retrieves a paginated list of users, allowing for sorting and conditional filtering based on a specific search term.

**URL:** `/api/v1/admin/user`  
**Method:** `GET`  
**Authentication:** Required (Admin Role) - Bearer Token

### Query Parameters

| Parameter     | Type      | Required | Default     | Description |
|---------------|-----------|----------|-------------|-------------|
| `page`        | `integer` | No       | `0`         | The zero-indexed page number to retrieve. |
| `size`        | `integer` | No       | `5`         | The number of records per page. |
| `sortBy`      | `string`  | No       | `firstName` | The field to sort the records by (e.g., `firstName`, `createdAt`). |
| `sortDir`     | `string`  | No       | `asc`       | The direction of the sort (`asc` or `desc`). |
| `searchType`  | `string`  | No       | -           | The specific field to search within (e.g., `userId`, `name`, `email`, `phone`). |
| `searchQuery` | `string`  | No       | -           | The actual text to search for within the specified `searchType` field. |

### Expected Request Examples

- **Default Load:** `GET /api/v1/admin/user?page=0&size=5&sortBy=firstName&sortDir=asc`
- **Searching by Email:** `GET /api/v1/admin/user?page=0&size=5&sortBy=firstName&sortDir=asc&searchType=email&searchQuery=alexander`
- **Sorting Newest First:** `GET /api/v1/admin/user?page=0&size=5&sortBy=createdAt&sortDir=desc`

### Expected Response

```json
{
  "status": "success",
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "userId": 1,
        "authId": 101,
        "firstName": "Alexander",
        "lastName": "Rivera",
        "email": "alexander.r@example.com",
        "phone": "9876543201",
        "gender": "MALE",
        "dob": "1990-01-01",
        "status": "ACTIVE",
        "role": "ROLE_USER",
        "createdAt": "2026-01-01T10:00:00Z",
        "updatedAt": "2026-01-01T10:00:00Z"
      }
    ],
    "pageable": {
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 5,
      "unpaged": false,
      "paged": true
    },
    "last": false,
    "totalElements": 25,
    "totalPages": 5,
    "size": 5,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
}
```
