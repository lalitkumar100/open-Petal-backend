package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * REST Controller for user queries (help/support).
 * Provides endpoints for users to submit and view their support queries.
 */
@RestController
@RequestMapping("/api/v1/queries")
public class UserQueryController {

    private final UserQueryService userQueryService;
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public UserQueryController(UserQueryService userQueryService, AuthService authService, UserService userService) {
        this.userQueryService = userQueryService;
        this.authService = authService;
        this.userService = userService;
    }

    private Long authenticateAndGetUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }
        String email = authService.getEmailFromToken(token);
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"))
                .getId();
    }

    /**
     * Submits a new support query to the platform admins.
     *
     * @param requestDto the payload containing the query details
     * @param authHeader the Authorization header containing the JWT token
     * @return ResponseEntity containing the created UserQuery
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserQuery>> submitQuery(
            @Valid @RequestBody UserQueryRequestDto requestDto, 
            @RequestHeader("Authorization") String authHeader) {
        Long userId = authenticateAndGetUserId(authHeader);
        UserQuery createdQuery = userQueryService.submitQuery(userId, requestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Query submitted successfully", createdQuery));
    }

    /**
     * Retrieves a specific query by its ID.
     *
     * @param id the unique identifier of the query
     * @return ResponseEntity containing the UserQuery
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserQuery>> getQueryById(@PathVariable Long id) {
        UserQuery query = userQueryService.getQueryById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Query retrieved successfully", query));
    }
}
