package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.user.UserProfileDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateProfileRequestDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateProfileResponseDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateStatusRequestDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateStatusResponseDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import org.springframework.http.HttpStatus;
import com.crimsonlogic.open_petal_backend.dto.user.ChangePasswordRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        UserProfileDto profileDto = UserProfileDto.builder()
                .userId(user.getId())
                .authId(user.getLogin().getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .gender(user.getGender())
                .status(user.getLogin().getStatus())
                .role(user.getLogin().getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", profileDto));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                              @Valid @RequestBody ChangePasswordRequestDto request) {
        String email = authenticateAndGetEmail(authHeader);
        authService.changePassword(email, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UpdateProfileResponseDto>> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                               @Valid @RequestBody UpdateProfileRequestDto request) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        User userDetails = new User();
        userDetails.setFirstName(request.getFirstName());
        userDetails.setLastName(request.getLastName());
        userDetails.setPhone(request.getPhone());
        userDetails.setDob(request.getDob());
        userDetails.setGender(request.getGender());

        User updatedUser = userService.updateUser(user.getId(), userDetails);

        UpdateProfileResponseDto responseDto = UpdateProfileResponseDto.builder()
                .userId(updatedUser.getId())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .dob(updatedUser.getDob())
                .gender(updatedUser.getGender())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();

        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", responseDto));
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<UpdateStatusResponseDto>> updateStatus(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                             @Valid @RequestBody UpdateStatusRequestDto request) {
        String email = authenticateAndGetEmail(authHeader);
        
        if (request.getStatus() == AccountStatus.BLOCKED) {
            throw new CustomException("Standard users cannot block their own accounts.", HttpStatus.BAD_REQUEST);
        }
        
        Login login = authService.changeSelfStatus(email, request.getStatus());
        
        UpdateStatusResponseDto responseDto = UpdateStatusResponseDto.builder()
                .authId(login.getId())
                .status(login.getStatus())
                .updatedAt(login.getUpdatedAt())
                .build();
                
        return ResponseEntity.ok(new ApiResponse<>(true, "Account status updated to " + login.getStatus(), responseDto));
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
