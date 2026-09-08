package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.conflict.UserConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.ConflictService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing user conflicts.
 * Provides endpoints for users to view and manage their session conflicts.
 */
@RestController
@RequestMapping("/api/v1/conflicts")
@RequiredArgsConstructor
public class ConflictController {

    private final ConflictService conflictService;
    private final AuthService authService;
    private final UserService userService;

    /**
     * Retrieves all conflicts associated with the authenticated user.
     *
     * @param authHeader the authorization header containing the user token
     * @return ResponseEntity containing a list of UserConflictSummaryResponseDto wrapped in an ApiResponse
     */
    @GetMapping("/my-conflicts")
    public ResponseEntity<ApiResponse<List<UserConflictSummaryResponseDto>>> getMyConflicts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = authenticateAndGetUserId(authHeader);
        List<UserConflictSummaryResponseDto> conflicts = conflictService.getMyConflicts(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched my conflicts successfully", conflicts));
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
}
