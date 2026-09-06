package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.SendConnectionRequestDto;
import com.crimsonlogic.open_petal_backend.entity.ConnectionRequest;
import com.crimsonlogic.open_petal_backend.service.ConnectionRequestService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/connections")
@RequiredArgsConstructor
public class ConnectionRequestController {

    private final ConnectionRequestService connectionRequestService;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<?> sendConnectionRequest(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                   @RequestBody SendConnectionRequestDto requestDto) {
        try {
            String email = authenticateAndGetEmail(authHeader);
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
            
            ConnectionRequest request = connectionRequestService.sendConnectionRequest(user.getId(), requestDto);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Connection request sent successfully");
            result.put("data", request.getId());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptConnectionRequest(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                     @PathVariable Long requestId) {
        try {
            String email = authenticateAndGetEmail(authHeader);
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
                    
            ConnectionRequest request = connectionRequestService.acceptConnectionRequest(user.getId(), requestId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Connection request accepted successfully");
            result.put("data", request.getId());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
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
