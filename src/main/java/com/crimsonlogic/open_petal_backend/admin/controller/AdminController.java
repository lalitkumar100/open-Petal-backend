package com.crimsonlogic.open_petal_backend.admin.controller;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillResponseDto;

import com.crimsonlogic.open_petal_backend.admin.dto.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.admin.dto.AdminUserDetailsDto;
import com.crimsonlogic.open_petal_backend.admin.dto.AdminUserDetailsDto.SkillSummaryDto;
import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.common.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.common.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.query.dto.AdminQueryReplyDto;
import com.crimsonlogic.open_petal_backend.query.dto.UserQueryResponseDto;
import com.crimsonlogic.open_petal_backend.query.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.query.enums.QueryStatus;
import com.crimsonlogic.open_petal_backend.query.service.UserQueryService;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillCategoryResponseDto;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillDetailResponseDto;
import com.crimsonlogic.open_petal_backend.skill.entity.Skill;
import com.crimsonlogic.open_petal_backend.skill.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.skill.service.LearningGoalService;
import com.crimsonlogic.open_petal_backend.skill.service.SkillCategoryService;
import com.crimsonlogic.open_petal_backend.skill.service.SkillService;
import com.crimsonlogic.open_petal_backend.skill.service.UserSkillService;
import com.crimsonlogic.open_petal_backend.user.dto.UserProfileDto;
import com.crimsonlogic.open_petal_backend.user.dto.UserStatusUpdateDto;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final MessageService messageService;

    private final AuthService authService;
    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserSkillService userSkillService;
    private final LearningGoalService learningGoalService;
    private final SkillCategoryService skillCategoryService;
    private final SkillService skillService;

    public AdminController(AuthService authService, UserService userService, UserQueryService userQueryService,
                           UserSkillService userSkillService, LearningGoalService learningGoalService,
                           SkillCategoryService skillCategoryService, SkillService skillService,
                           MessageService messageService) {
        this.messageService = messageService;
        this.authService = authService;
        this.skillCategoryService = skillCategoryService;
        this.userService = userService;
        this.userQueryService = userQueryService;
        this.userSkillService = userSkillService;
        this.learningGoalService = learningGoalService;
        this.skillService = skillService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Void>> getAdminDashboard(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        authenticateAndGetEmail(authHeader);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.ADMIN_DASHBOARD_WELCOME,
                messageService.getMessage(MessageCodes.ADMIN_DASHBOARD_WELCOME),
                null));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<UserProfileDto>>> getPaginatedUsers(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchQuery) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        Page<UserProfileDto> users = userService.getPaginatedUsers(page, size, sortBy, sortDir, searchType, searchQuery)
                .map(this::mapToDto);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_USERS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_USERS_RETRIEVED), users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserById(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                   @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
                
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_USER_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_USER_RETRIEVED), mapToDto(user)));
    }

    @GetMapping("/users/{id}/details")
    public ResponseEntity<ApiResponse<AdminUserDetailsDto>> getUserDetailsByAdmin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
                
        UserProfileDto userInfo = mapToDto(user);
        
        List<SkillSummaryDto> skillsOffered = userSkillService.getAllSkillsByUser(id).stream()
                .map(us -> new SkillSummaryDto(us.getSkill().getName(), us.getSkillLevel().name()))
                .collect(Collectors.toList());
                
        List<SkillSummaryDto> learningGoals = learningGoalService.getLearningGoalsByUserId(id).stream()
                .map(lg -> new SkillSummaryDto(lg.getSkill().getName(), lg.getTargetLevel().name()))
                .collect(Collectors.toList());
                
        AdminUserDetailsDto detailsDto = AdminUserDetailsDto.builder()
                .userInfo(userInfo)
                .skillsOffered(skillsOffered)
                .learningGoals(learningGoals)
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_USER_DETAILS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_USER_DETAILS_RETRIEVED), detailsDto));
    }

    @PostMapping("/skill-categories")
    public ResponseEntity<ApiResponse<SkillCategoryResponseDto>> createSkillCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody SkillCategoryRequestDto dto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        SkillCategoryResponseDto created = SkillCategoryResponseDto.from(skillCategoryService.createCategory(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageCodes.ADMIN_SKILL_CATEGORY_CREATED, messageService.getMessage(MessageCodes.ADMIN_SKILL_CATEGORY_CREATED), created));
    }

    @GetMapping("/skill-categories/{id}/skills")
    public ResponseEntity<ApiResponse<Page<SkillResponseDto>>> getSkillsByCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String searchQuery) {
        authenticateAndGetEmail(authHeader); // Validate admin
        Page<SkillResponseDto> skills = skillService.getSkillsByCategory(id, page, size, searchQuery);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_SKILLS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_SKILLS_RETRIEVED), skills));
    }

    @GetMapping("/skills/{id}/details")
    public ResponseEntity<ApiResponse<SkillDetailResponseDto>> getSkillDetailsByAdmin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        SkillDetailResponseDto details = skillService.getAdminSkillDetails(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_SKILL_DETAILS_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_SKILL_DETAILS_RETRIEVED), details));
    }

    @PostMapping("/skills")
    public ResponseEntity<ApiResponse<SkillResponseDto>> addSkillToCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AddSkillRequestDto dto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        SkillResponseDto created = skillService.addSkillToCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageCodes.ADMIN_SKILL_ADDED, messageService.getMessage(MessageCodes.ADMIN_SKILL_ADDED), created));
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<Void>> blockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                         @PathVariable Long id,
                                                         @Valid @RequestBody(required = false) UserStatusUpdateDto statusUpdateDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        String reason = (statusUpdateDto != null) ? statusUpdateDto.getReason() : null;
        userService.blockUser(id, reason);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_USER_BLOCKED, messageService.getMessage(MessageCodes.ADMIN_USER_BLOCKED), null));
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                           @PathVariable Long id,
                                                           @Valid @RequestBody(required = false) UserStatusUpdateDto statusUpdateDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        String reason = (statusUpdateDto != null) ? statusUpdateDto.getReason() : null;
        userService.unblockUser(id, reason);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_USER_UNBLOCKED, messageService.getMessage(MessageCodes.ADMIN_USER_UNBLOCKED), null));
    }

    @GetMapping("/queries")
    public ResponseEntity<ApiResponse<List<UserQueryResponseDto>>> getAllQueries(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) QueryStatus status) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        List<UserQueryResponseDto> queries = ((status != null)
                ? userQueryService.getQueriesByStatus(status) 
                : userQueryService.getAllQueries()).stream()
                .map(UserQueryResponseDto::from)
                .toList();
                
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_QUERIES_RETRIEVED, messageService.getMessage(MessageCodes.ADMIN_QUERIES_RETRIEVED), queries));
    }

    @PatchMapping("/queries/{queryId}/reply")
    public ResponseEntity<ApiResponse<UserQueryResponseDto>> replyToQuery(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long queryId,
            @Valid @RequestBody AdminQueryReplyDto replyDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        UserQueryResponseDto updatedQuery = UserQueryResponseDto.from(
                userQueryService.adminReplyToQuery(queryId, replyDto));
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.ADMIN_QUERY_REPLIED, messageService.getMessage(MessageCodes.ADMIN_QUERY_REPLIED), updatedQuery));
    }

    private UserProfileDto mapToDto(User user) {
        return UserProfileDto.builder()
                .userId(user.getId())
                .authId(user.getLogin() != null ? user.getLogin().getId() : null)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
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
