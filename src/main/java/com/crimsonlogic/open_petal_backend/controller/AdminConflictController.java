package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
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

@RestController
@RequestMapping("/api/v1/admin/conflicts")
@RequiredArgsConstructor
public class AdminConflictController {

    private final AdminConflictService adminConflictService;
    private final AuthService authService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminConflictSummaryResponseDto>>> getAllConflicts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        verifyAdminRole(authHeader);
        List<AdminConflictSummaryResponseDto> conflicts = adminConflictService.getAllConflictsForAdmin();
        return ResponseEntity.ok(new ApiResponse<>(true, "Global conflicts retrieved", conflicts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminConflictDetailResponseDto>> getConflictDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        verifyAdminRole(authHeader);
        AdminConflictDetailResponseDto detail = adminConflictService.getAdminConflictDetails(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Conflict details retrieved", detail));
    }

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
