package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
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

@RestController
@RequestMapping("/api/v1/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionService sessionService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminSessionSummaryResponseDto>>> getAllSessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        verifyAdminRole(authHeader);
        List<AdminSessionSummaryResponseDto> sessions = sessionService.getAllGlobalSessions();
        return ResponseEntity.ok(new ApiResponse<>(true, "Global sessions retrieved", sessions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminSessionDetailResponseDto>> getSessionDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        verifyAdminRole(authHeader);
        AdminSessionDetailResponseDto detail = sessionService.getAdminSessionDetails(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session details retrieved", detail));
    }

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
