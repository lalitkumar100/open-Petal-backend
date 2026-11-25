package com.crimsonlogic.open_petal_backend.session.controller;

import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.session.dto.UserConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.session.service.ConflictService;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conflicts")
@RequiredArgsConstructor
public class ConflictController {

    private final MessageService messageService;

    private final ConflictService conflictService;
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/my-conflicts")
    public ResponseEntity<ApiResponse<List<UserConflictSummaryResponseDto>>> getMyConflicts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = authenticateAndGetUserId(authHeader);
        List<UserConflictSummaryResponseDto> conflicts = conflictService.getMyConflicts(userId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_CONFLICTS_RETRIEVED, messageService.getMessage(MessageCodes.USER_CONFLICTS_RETRIEVED), conflicts));
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
