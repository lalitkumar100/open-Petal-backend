package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.user.UserProfileDto;
import com.crimsonlogic.open_petal_backend.dto.user.UserStatusUpdateDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.dto.query.AdminQueryReplyDto;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.enums.QueryStatus;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AuthService authService;
    private final UserService userService;
    private final UserQueryService userQueryService;

    public AdminController(AuthService authService, UserService userService, UserQueryService userQueryService) {
        this.authService = authService;
        this.userService = userService;
        this.userQueryService = userQueryService;
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

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<String>> blockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                         @PathVariable Long id,
                                                         @RequestBody(required = false) UserStatusUpdateDto statusUpdateDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        String reason = (statusUpdateDto != null) ? statusUpdateDto.getReason() : null;
        userService.blockUser(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "User blocked successfully", null));
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<ApiResponse<String>> unblockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                           @PathVariable Long id,
                                                           @RequestBody(required = false) UserStatusUpdateDto statusUpdateDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        String reason = (statusUpdateDto != null) ? statusUpdateDto.getReason() : null;
        userService.unblockUser(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "User unblocked successfully", null));
    }

    @GetMapping("/queries")
    public ResponseEntity<ApiResponse<List<UserQuery>>> getAllQueries(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) QueryStatus status) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        List<UserQuery> queries = (status != null) 
                ? userQueryService.getQueriesByStatus(status) 
                : userQueryService.getAllQueries();
                
        return ResponseEntity.ok(new ApiResponse<>(true, "Queries retrieved successfully", queries));
    }

    @PatchMapping("/queries/{queryId}/reply")
    public ResponseEntity<ApiResponse<UserQuery>> replyToQuery(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long queryId,
            @RequestBody AdminQueryReplyDto replyDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        UserQuery updatedQuery = userQueryService.adminReplyToQuery(queryId, replyDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Query replied successfully", updatedQuery));
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
