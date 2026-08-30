package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.UserProfileDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AuthService authService;
    private final UserService userService;

    public AdminController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        return ResponseEntity.ok("Welcome Admin! Your email is: " + email);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getAllUsers(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        List<UserProfileDto> users = userService.getAllUsers().stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserById(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                   @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
                
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", mapToDto(user)));
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<String>> blockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                         @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        userService.blockUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User blocked successfully", null));
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<ApiResponse<String>> unblockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                           @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        userService.unblockUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User unblocked successfully", null));
    }

    private UserProfileDto mapToDto(User user) {
        return UserProfileDto.builder()
                .userId(user.getId())
                .authId(user.getLogin() != null ? user.getLogin().getId() : null)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .gender(user.getGender())
                .status(user.getLogin() != null ? user.getLogin().getStatus() : null)
                .role(user.getLogin() != null ? user.getLogin().getRole() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
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
        if (!"ROLE_ADMIN".equals(role)) {
            throw new AuthorizationException("Admin access required");
        }

        return authService.getEmailFromToken(token);
    }
}
