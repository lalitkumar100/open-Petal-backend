package com.crimsonlogic.open_petal_backend.admin.controller;

import com.crimsonlogic.open_petal_backend.admin.dto.AdminCancelSessionRequestDto;
import com.crimsonlogic.open_petal_backend.admin.dto.AdminSessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.admin.dto.AdminSessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.session.service.SessionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final MessageService messageService;

    private final SessionService sessionService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminSessionSummaryResponseDto>>> getAllSessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        verifyAdminRole(authHeader);
        List<AdminSessionSummaryResponseDto> sessions = sessionService.getAllGlobalSessions();
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_SESSIONS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_SESSIONS_RETRIEVED), sessions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminSessionDetailResponseDto>> getSessionDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        verifyAdminRole(authHeader);
        AdminSessionDetailResponseDto detail = sessionService.getAdminSessionDetails(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_SESSION_DETAILS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_SESSION_DETAILS_RETRIEVED), detail));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody(required = false) AdminCancelSessionRequestDto request) {
        verifyAdminRole(authHeader);
        String reason = (request != null && request.getReason() != null && !request.getReason().trim().isEmpty())
                ? request.getReason().trim()
                : "Cancelled by Administrator";
        sessionService.adminCancelSession(id, reason);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_SESSION_CANCELLED, messageService.getMessage(MessageCodes.ADMIN_SESSION_CANCELLED), null));
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
