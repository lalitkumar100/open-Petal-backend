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
import com.crimsonlogic.open_petal_backend.service.UserSkillService;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminUserDetailsDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminUserDetailsDto.SkillSummaryDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.dto.skill.SkillDetailResponseDto;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.service.SkillCategoryService;
import com.crimsonlogic.open_petal_backend.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AuthService authService;
    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserSkillService userSkillService;
    private final LearningGoalService learningGoalService;
    private final SkillCategoryService skillCategoryService;
    private final SkillService skillService;

    public AdminController(AuthService authService, UserService userService, UserQueryService userQueryService,
                           UserSkillService userSkillService, LearningGoalService learningGoalService,
                           SkillCategoryService skillCategoryService, SkillService skillService) {
        this.authService = authService;
        this.skillCategoryService = skillCategoryService;
        this.userService = userService;
        this.userQueryService = userQueryService;
        this.userSkillService = userSkillService;
        this.learningGoalService = learningGoalService;
        this.skillService = skillService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = authenticateAndGetEmail(authHeader);
        return ResponseEntity.ok("Welcome Admin! Your email is: " + email);
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
                
        return ResponseEntity.ok(new ApiResponse<>(true, "User details retrieved successfully", detailsDto));
    }

    @PostMapping("/skill-categories")
    public ResponseEntity<ApiResponse<SkillCategory>> createSkillCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody SkillCategoryRequestDto dto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        SkillCategory created = skillCategoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Skill category created successfully", created));
    }

    @GetMapping("/skill-categories/{id}/skills")
    public ResponseEntity<ApiResponse<Page<ApiResponse.SkillDTO>>> getSkillsByCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String searchQuery) {
        authenticateAndGetEmail(authHeader); // Validate admin
        Page<ApiResponse.SkillDTO> skills = skillService.getSkillsByCategory(id, page, size, searchQuery);
        return ResponseEntity.ok(new ApiResponse<>(true, "Skills retrieved successfully", skills));
    }

    @GetMapping("/skills/{id}/details")
    public ResponseEntity<ApiResponse<SkillDetailResponseDto>> getSkillDetailsByAdmin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        SkillDetailResponseDto details = skillService.getAdminSkillDetails(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Skill details retrieved successfully", details));
    }

    @PostMapping("/skills")
    public ResponseEntity<ApiResponse<ApiResponse.SkillDTO>> addSkillToCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AddSkillRequestDto dto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        ApiResponse.SkillDTO created = skillService.addSkillToCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Skill added successfully", created));
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
