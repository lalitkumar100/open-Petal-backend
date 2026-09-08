package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminCancelSessionRequestDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for administrative session management.
 * Provides endpoints for admins to view all global sessions and force-cancel them.
 */
@RestController
@RequestMapping("/api/v1/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionService sessionService;
    private final AuthService authService;

    /**
     * Retrieves a global list of all sessions across the platform.
     *
     * @param authHeader the authorization header containing the admin token
     * @return ResponseEntity containing a list of AdminSessionSummaryResponseDto wrapped in an ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminSessionSummaryResponseDto>>> getAllSessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        verifyAdminRole(authHeader);
        List<AdminSessionSummaryResponseDto> sessions = sessionService.getAllGlobalSessions();
        return ResponseEntity.ok(new ApiResponse<>(true, "Global sessions retrieved", sessions));
    }

    /**
     * Retrieves detailed information about a specific session for admins.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the session
     * @return ResponseEntity containing AdminSessionDetailResponseDto wrapped in an ApiResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminSessionDetailResponseDto>> getSessionDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        verifyAdminRole(authHeader);
        AdminSessionDetailResponseDto detail = sessionService.getAdminSessionDetails(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session details retrieved", detail));
    }

    /**
     * Force-cancels a session administratively.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the session to cancel
     * @param request the payload containing the cancellation reason
     * @return ResponseEntity indicating the success of the cancellation
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody(required = false) AdminCancelSessionRequestDto request) {
        verifyAdminRole(authHeader);
        String reason = (request != null && request.getReason() != null && !request.getReason().trim().isEmpty())
                ? request.getReason().trim()
                : "Cancelled by Administrator";
        sessionService.adminCancelSession(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session force-cancelled successfully", null));
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
}
