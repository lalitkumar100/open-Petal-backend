package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.user.UserProfileDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateProfileRequestDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateProfileResponseDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateStatusRequestDto;
import com.crimsonlogic.open_petal_backend.dto.skill.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.dto.skill.CreateLearningGoalDto;
import com.crimsonlogic.open_petal_backend.dto.user.UpdateStatusResponseDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import org.springframework.http.HttpStatus;
import com.crimsonlogic.open_petal_backend.dto.user.ChangePasswordRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot;

import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;
import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.service.UserSkillService;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import com.crimsonlogic.open_petal_backend.dto.skill.UserSkillResponseDto;
import com.crimsonlogic.open_petal_backend.dto.skill.LearningGoalResponseDto;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserSkillService userSkillService;
    private final LearningGoalService learningGoalService;

    public UserController(AuthService authService, UserService userService, UserQueryService userQueryService,
                          UserSkillService userSkillService, LearningGoalService learningGoalService) {
        this.authService = authService;
        this.userService = userService;
        this.userQueryService = userQueryService;
        this.userSkillService = userSkillService;
        this.learningGoalService = learningGoalService;
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
                .phone(user.getPhone())
                .dob(user.getDob())
                .gender(user.getGender())
                .description(user.getDescription())
                .credit(user.getCredit())
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
        userDetails.setDescription(request.getDescription());

        User updatedUser = userService.updateUser(user.getId(), userDetails);

        UpdateProfileResponseDto responseDto = UpdateProfileResponseDto.builder()
                .userId(updatedUser.getId())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .dob(updatedUser.getDob())
                .gender(updatedUser.getGender())
                .description(updatedUser.getDescription())
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

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> getUserAvailabilitySlots(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<AvailabilitySlot> slots = userService.getUserAvailabilitySlots(user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability slots retrieved successfully", slots));
    }

    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> addAvailabilitySlot(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AvailabilitySlot newSlot) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<AvailabilitySlot> updatedSlots = userService.addAvailabilitySlot(user.getId(), newSlot);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability slot added successfully", updatedSlots));
    }

    @DeleteMapping("/availability")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> removeAvailabilitySlot(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody AvailabilitySlot slotToRemove) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<AvailabilitySlot> updatedSlots = userService.removeAvailabilitySlot(user.getId(), slotToRemove);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability slot removed successfully", updatedSlots));
    }

    @PostMapping("/queries")
    public ResponseEntity<ApiResponse<UserQuery>> submitQuery(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UserQueryRequestDto requestDto) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        UserQuery createdQuery = userQueryService.submitQuery(user.getId(), requestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Query submitted successfully", createdQuery));
    }

    @GetMapping("/queries")
    public ResponseEntity<ApiResponse<List<UserQuery>>> getAllMyQueries(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        List<UserQuery> queries = userQueryService.getQueriesByUser(user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "User queries retrieved successfully", queries));
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<UserSkillResponseDto>>> getMyUserSkills(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        List<UserSkillResponseDto> skills = userSkillService.getAllSkillsByUser(user.getId())
                .stream().map(this::mapToUserSkillDto).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "User skills retrieved successfully", skills));
    }

    @GetMapping("/learning-goals")
    public ResponseEntity<ApiResponse<List<LearningGoalResponseDto>>> getMyLearningGoals(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        List<LearningGoalResponseDto> goals = learningGoalService.getLearningGoalsByUserId(user.getId())
                .stream().map(this::mapToLearningGoalDto).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "User learning goals retrieved successfully", goals));
    }

    @PostMapping("/skills/teach")
    public ResponseEntity<ApiResponse<UserSkillResponseDto>> addTeachSkill(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateUserSkillDto requestDto) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        UserSkill createdSkill = userSkillService.addSkillToUser(user.getId(), requestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Teaching skill added successfully", mapToUserSkillDto(createdSkill)));
    }

    @PostMapping("/skills/learn")
    public ResponseEntity<ApiResponse<LearningGoalResponseDto>> addLearningGoal(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateLearningGoalDto requestDto) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        LearningGoal createdGoal = learningGoalService.createLearningGoal(user.getId(), requestDto.getSkillId(), requestDto.getCurrentLevel(), requestDto.getTargetLevel());
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal added successfully", mapToLearningGoalDto(createdGoal)));
    }

    @DeleteMapping("/skills/teach/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeachSkill(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        userSkillService.removeSkillFromUser(user.getId(), id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Teaching skill deleted successfully", null));
    }

    @DeleteMapping("/skills/learn/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLearningGoal(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        learningGoalService.deleteLearningGoal(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal deleted successfully", null));
    }

    private UserSkillResponseDto mapToUserSkillDto(UserSkill userSkill) {
        return UserSkillResponseDto.builder()
                .id(userSkill.getId())
                .skillId(userSkill.getSkill().getId())
                .skillName(userSkill.getSkill().getName())
                .level(userSkill.getSkillLevel())
                .experienceYears(userSkill.getExperienceYears())
                .teachingMode(userSkill.getTeachingMode())
                .sessionDuration(userSkill.getSessionDurationMin())
                .creditsPerSession(userSkill.getCreditsPerSession())
                .rating(userSkill.getAvgSkillRating() != null ? userSkill.getAvgSkillRating().doubleValue() : 0.0)
                .peopleTaught(userSkill.getLearnersTaughtCount())
                .isActive(!userSkill.getIsDeleted())
                .build();
    }

    private LearningGoalResponseDto mapToLearningGoalDto(LearningGoal learningGoal) {
        return LearningGoalResponseDto.builder()
                .id(learningGoal.getId())
                .skillId(learningGoal.getSkill().getId())
                .skillName(learningGoal.getSkill().getName())
                .currentLevel(learningGoal.getCurrentLevel())
                .targetLevel(learningGoal.getTargetLevel())
                .isActive(!learningGoal.getIsDeleted())
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
        if (!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role)) {
            throw new AuthorizationException("Insufficient privileges");
        }

        return authService.getEmailFromToken(token);
    }
}
