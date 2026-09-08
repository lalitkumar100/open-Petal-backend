package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.ResolveConflictRequestDto;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.service.AdminConflictService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for administrative conflict management.
 * Provides endpoints for admins to view and resolve session conflicts.
 */
@RestController
@RequestMapping("/api/v1/admin/conflicts")
@RequiredArgsConstructor
public class AdminConflictController {

    private final AdminConflictService adminConflictService;
    private final AuthService authService;
    private final UserService userService;

    /**
     * Retrieves a global list of all conflicts.
     *
     * @param authHeader the authorization header containing the admin token
     * @return ResponseEntity containing a list of AdminConflictSummaryResponseDto wrapped in an ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminConflictSummaryResponseDto>>> getAllConflicts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        verifyAdminRole(authHeader);
        List<AdminConflictSummaryResponseDto> conflicts = adminConflictService.getAllConflictsForAdmin();
        return ResponseEntity.ok(new ApiResponse<>(true, "Global conflicts retrieved", conflicts));
    }

    /**
     * Retrieves detailed information about a specific conflict.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the conflict
     * @return ResponseEntity containing AdminConflictDetailResponseDto wrapped in an ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminConflictDetailResponseDto>> getConflictDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        verifyAdminRole(authHeader);
        AdminConflictDetailResponseDto detail = adminConflictService.getAdminConflictDetails(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Conflict details retrieved", detail));
    }

    /**
     * Resolves a specific conflict.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the conflict to resolve
     * @param request the resolution payload
     * @return ResponseEntity indicating the success of the resolution
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<String>> resolveConflict(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody ResolveConflictRequestDto request) {
        Long adminUserId = verifyAdminRoleAndGetId(authHeader);
        adminConflictService.resolveConflict(id, adminUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Conflict resolved successfully", null));
    }

    private void verifyAdminRole(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }
        String role = authService.getRoleFromToken(token);
        if (!"ROLE_ADMIN".equals(role)) {
            throw new AuthorizationException("Admin access required");
        }
    }

    private Long verifyAdminRoleAndGetId(String authHeader) {
        verifyAdminRole(authHeader);
        String token = authHeader.substring(7);
        String email = authService.getEmailFromToken(token);
        return userService.getUserByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Admin user not found"))
                .getId();
    }
}
