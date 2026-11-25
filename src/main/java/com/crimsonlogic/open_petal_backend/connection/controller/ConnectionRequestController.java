package com.crimsonlogic.open_petal_backend.connection.controller;

import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.common.exception.CustomException;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.connection.dto.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.connection.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.connection.service.ConnectionRequestService;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/connections")
@RequiredArgsConstructor
public class ConnectionRequestController {

    private final ConnectionRequestService connectionRequestService;
    private final AuthService authService;
    private final UserService userService;
    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Long>> sendConnectionRequest(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody SendConnectionRequestDto requestDto) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        ConnectionRequest request = connectionRequestService.sendConnectionRequest(user.getId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.CONNECTION_REQUEST_SENT,
                messageService.getMessage(MessageCodes.CONNECTION_REQUEST_SENT),
                request.getId()));
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<ApiResponse<Long>> acceptConnectionRequest(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long requestId) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        ConnectionRequest request = connectionRequestService.acceptConnectionRequest(user.getId(), requestId);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.CONNECTION_REQUEST_ACCEPTED,
                messageService.getMessage(MessageCodes.CONNECTION_REQUEST_ACCEPTED),
                request.getId()));
    }

    private String authenticateAndGetEmail(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }

        String role = authService.getRoleFromToken(token);
        if (!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role)) {
            throw new AuthorizationException("Insufficient privileges");
        }

        return authService.getEmailFromToken(token);
    }
}
