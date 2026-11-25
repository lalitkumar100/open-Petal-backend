package com.crimsonlogic.open_petal_backend.user.controller;

import com.crimsonlogic.open_petal_backend.auth.entity.Login;
import com.crimsonlogic.open_petal_backend.auth.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.common.exception.CustomException;
import com.crimsonlogic.open_petal_backend.query.dto.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.query.dto.UserQueryResponseDto;
import com.crimsonlogic.open_petal_backend.query.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.query.service.UserQueryService;
import com.crimsonlogic.open_petal_backend.skill.dto.CreateLearningGoalDto;
import com.crimsonlogic.open_petal_backend.skill.dto.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.skill.dto.LearningGoalResponseDto;
import com.crimsonlogic.open_petal_backend.skill.dto.UserSkillResponseDto;
import com.crimsonlogic.open_petal_backend.skill.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.skill.entity.UserSkill;
import com.crimsonlogic.open_petal_backend.skill.service.LearningGoalService;
import com.crimsonlogic.open_petal_backend.skill.service.UserSkillService;
import com.crimsonlogic.open_petal_backend.user.dto.AvailabilitySlot;
import com.crimsonlogic.open_petal_backend.user.dto.ChangePasswordRequestDto;
import com.crimsonlogic.open_petal_backend.user.dto.UpdateProfileRequestDto;
import com.crimsonlogic.open_petal_backend.user.dto.UpdateProfileResponseDto;
import com.crimsonlogic.open_petal_backend.user.dto.UpdateStatusRequestDto;
import com.crimsonlogic.open_petal_backend.user.dto.UpdateStatusResponseDto;
import com.crimsonlogic.open_petal_backend.user.dto.UserProfileDto;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final MessageService messageService;

    private final AuthService authService;
    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserSkillService userSkillService;
    private final LearningGoalService learningGoalService;

    public UserController(AuthService authService, UserService userService, UserQueryService userQueryService,
                          UserSkillService userSkillService, LearningGoalService learningGoalService,
                           MessageService messageService) {
        this.messageService = messageService;
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

        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_PROFILE_RETRIEVED, messageService.getMessage(MessageCodes.USER_PROFILE_RETRIEVED), profileDto));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                              @Valid @RequestBody ChangePasswordRequestDto request) {
        String email = authenticateAndGetEmail(authHeader);
        authService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_PASSWORD_CHANGED, messageService.getMessage(MessageCodes.USER_PASSWORD_CHANGED), null));
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

        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_PROFILE_UPDATED, messageService.getMessage(MessageCodes.USER_PROFILE_UPDATED), responseDto));
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
                
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_STATUS_UPDATED, messageService.getMessage(MessageCodes.USER_STATUS_UPDATED, login.getStatus()), responseDto));
    }

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> getUserAvailabilitySlots(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<AvailabilitySlot> slots = userService.getUserAvailabilitySlots(user.getId());
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_AVAILABILITY_RETRIEVED, messageService.getMessage(MessageCodes.USER_AVAILABILITY_RETRIEVED), slots));
    }

    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> addAvailabilitySlot(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AvailabilitySlot newSlot) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<AvailabilitySlot> updatedSlots = userService.addAvailabilitySlot(user.getId(), newSlot);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_AVAILABILITY_ADDED, messageService.getMessage(MessageCodes.USER_AVAILABILITY_ADDED), updatedSlots));
    }

    @DeleteMapping("/availability")
    public ResponseEntity<ApiResponse<List<AvailabilitySlot>>> removeAvailabilitySlot(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AvailabilitySlot slotToRemove) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<AvailabilitySlot> updatedSlots = userService.removeAvailabilitySlot(user.getId(), slotToRemove);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_AVAILABILITY_REMOVED, messageService.getMessage(MessageCodes.USER_AVAILABILITY_REMOVED), updatedSlots));
    }

    @PostMapping("/queries")
    public ResponseEntity<ApiResponse<UserQueryResponseDto>> submitQuery(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UserQueryRequestDto requestDto) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        UserQueryResponseDto createdQuery = UserQueryResponseDto.from(userQueryService.submitQuery(user.getId(), requestDto));
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.QUERY_SUBMITTED, messageService.getMessage(MessageCodes.QUERY_SUBMITTED), createdQuery));
    }

    @GetMapping("/queries")
    public ResponseEntity<ApiResponse<List<UserQueryResponseDto>>> getAllMyQueries(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        List<UserQueryResponseDto> queries = userQueryService.getQueriesByUser(user.getId()).stream()
                .map(UserQueryResponseDto::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_QUERIES_RETRIEVED, messageService.getMessage(MessageCodes.USER_QUERIES_RETRIEVED), queries));
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<UserSkillResponseDto>>> getMyUserSkills(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        List<UserSkillResponseDto> skills = userSkillService.getAllSkillsByUser(user.getId())
                .stream().map(this::mapToUserSkillDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_SKILLS_RETRIEVED, messageService.getMessage(MessageCodes.USER_SKILLS_RETRIEVED), skills));
    }

    @GetMapping("/learning-goals")
    public ResponseEntity<ApiResponse<List<LearningGoalResponseDto>>> getMyLearningGoals(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        List<LearningGoalResponseDto> goals = learningGoalService.getLearningGoalsByUserId(user.getId())
                .stream().map(this::mapToLearningGoalDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_LEARNING_GOALS_RETRIEVED, messageService.getMessage(MessageCodes.USER_LEARNING_GOALS_RETRIEVED), goals));
    }

    @PostMapping("/skills/teach")
    public ResponseEntity<ApiResponse<UserSkillResponseDto>> addTeachSkill(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateUserSkillDto requestDto) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        UserSkill createdSkill = userSkillService.addSkillToUser(user.getId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_TEACHING_SKILL_ADDED, messageService.getMessage(MessageCodes.USER_TEACHING_SKILL_ADDED), mapToUserSkillDto(createdSkill)));
    }

    @PostMapping("/skills/learn")
    public ResponseEntity<ApiResponse<LearningGoalResponseDto>> addLearningGoal(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CreateLearningGoalDto requestDto) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        LearningGoal createdGoal = learningGoalService.createLearningGoal(user.getId(), requestDto.getSkillId(), requestDto.getCurrentLevel(), requestDto.getTargetLevel());
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_LEARNING_GOAL_ADDED, messageService.getMessage(MessageCodes.USER_LEARNING_GOAL_ADDED), mapToLearningGoalDto(createdGoal)));
    }

    @DeleteMapping("/skills/teach/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeachSkill(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        userSkillService.removeSkillFromUser(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_TEACHING_SKILL_DELETED, messageService.getMessage(MessageCodes.USER_TEACHING_SKILL_DELETED), null));
    }

    @DeleteMapping("/skills/learn/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLearningGoal(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        String email = authenticateAndGetEmail(authHeader);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        
        learningGoalService.deleteLearningGoal(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.USER_LEARNING_GOAL_DELETED, messageService.getMessage(MessageCodes.USER_LEARNING_GOAL_DELETED), null));
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
                .roadplan(learningGoal.getRoadplan())
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
