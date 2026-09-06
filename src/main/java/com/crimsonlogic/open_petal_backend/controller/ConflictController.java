package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.conflict.UserConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.ConflictService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conflicts")
@RequiredArgsConstructor
public class ConflictController {

    private final ConflictService conflictService;
    private final AuthService authService;
    private final UserService userService;

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
