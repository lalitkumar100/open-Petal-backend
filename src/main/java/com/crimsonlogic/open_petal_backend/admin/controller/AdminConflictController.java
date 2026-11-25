package com.crimsonlogic.open_petal_backend.admin.controller;

import com.crimsonlogic.open_petal_backend.admin.dto.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.admin.dto.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.admin.dto.ResolveConflictRequestDto;
import com.crimsonlogic.open_petal_backend.admin.service.AdminConflictService;
import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/conflicts")
@RequiredArgsConstructor
public class AdminConflictController {

    private final MessageService messageService;

    private final AdminConflictService adminConflictService;
    private final AuthService authService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminConflictSummaryResponseDto>>> getAllConflicts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        verifyAdminRole(authHeader);
        List<AdminConflictSummaryResponseDto> conflicts = adminConflictService.getAllConflictsForAdmin();
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_CONFLICTS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_CONFLICTS_RETRIEVED), conflicts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminConflictDetailResponseDto>> getConflictDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        verifyAdminRole(authHeader);
        AdminConflictDetailResponseDto detail = adminConflictService.getAdminConflictDetails(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_CONFLICT_DETAILS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_CONFLICT_DETAILS_RETRIEVED), detail));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolveConflict(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody ResolveConflictRequestDto request) {
        Long adminUserId = verifyAdminRoleAndGetId(authHeader);
        adminConflictService.resolveConflict(id, adminUserId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_CONFLICT_RESOLVED, messageService.getMessage(MessageCodes.ADMIN_CONFLICT_RESOLVED), null));
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
