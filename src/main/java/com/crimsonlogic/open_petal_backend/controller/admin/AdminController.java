package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
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
import com.crimsonlogic.open_petal_backend.dto.admin.AdminDashboardDto;
import com.crimsonlogic.open_petal_backend.service.AdminDashboardService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for comprehensive administrative functions.
 * Provides endpoints for managing users, skills, categories, and queries.
 */
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
    private final AdminDashboardService adminDashboardService;

    public AdminController(AuthService authService, UserService userService, UserQueryService userQueryService,
                           UserSkillService userSkillService, LearningGoalService learningGoalService,
                           SkillCategoryService skillCategoryService, SkillService skillService,
                           AdminDashboardService adminDashboardService) {
        this.authService = authService;
        this.skillCategoryService = skillCategoryService;
        this.userService = userService;
        this.userQueryService = userQueryService;
        this.userSkillService = userSkillService;
        this.learningGoalService = learningGoalService;
        this.skillService = skillService;
        this.adminDashboardService = adminDashboardService;
    }

    /**
     * Retrieves the admin dashboard statistics.
     *
     * @param authHeader the authorization header containing the admin token
     * @return ResponseEntity containing dashboard stats
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getAdminDashboard(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        authenticateAndGetEmail(authHeader); // Validate admin
        AdminDashboardDto stats = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard stats retrieved successfully", stats));
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param authHeader the authorization header containing the admin token
     * @param page the page number to retrieve
     * @param size the number of records per page
     * @param sortBy the field to sort by
     * @param sortDir the direction of sorting
     * @param searchType the type of search (e.g., name, email)
     * @param searchQuery the search query string
     * @return ResponseEntity containing a paginated list of UserProfileDto wrapped in an ApiResponse
     */
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

    /**
     * Retrieves basic profile information for a specific user.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the user
     * @return ResponseEntity containing UserProfileDto wrapped in an ApiResponse
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserById(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                   @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
                
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", mapToDto(user)));
    }

    /**
     * Retrieves comprehensive details of a user, including skills and learning goals.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the user
     * @return ResponseEntity containing AdminUserDetailsDto wrapped in an ApiResponse
     */
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

    /**
     * Creates a new skill category.
     *
     * @param authHeader the authorization header containing the admin token
     * @param dto the request payload for creating the category
     * @return ResponseEntity containing the created SkillCategory wrapped in an ApiResponse
     */
    @PostMapping("/skill-categories")
    public ResponseEntity<ApiResponse<SkillCategory>> createSkillCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody SkillCategoryRequestDto dto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        SkillCategory created = skillCategoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Skill category created successfully", created));
    }

    /**
     * Retrieves all skills within a specific category.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the skill category
     * @param page the page number to retrieve
     * @param size the number of records per page
     * @param searchQuery optional search query to filter skills
     * @return ResponseEntity containing a paginated list of SkillDTOs wrapped in an ApiResponse
     */
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

    /**
     * Retrieves detailed information about a specific skill.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the skill
     * @return ResponseEntity containing SkillDetailResponseDto wrapped in an ApiResponse
     */
    @GetMapping("/skills/{id}/details")
    public ResponseEntity<ApiResponse<SkillDetailResponseDto>> getSkillDetailsByAdmin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        authenticateAndGetEmail(authHeader); // Validate admin
        SkillDetailResponseDto details = skillService.getAdminSkillDetails(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Skill details retrieved successfully", details));
    }

    /**
     * Adds a new skill to an existing category.
     *
     * @param authHeader the authorization header containing the admin token
     * @param dto the request payload for the new skill
     * @return ResponseEntity containing the created SkillDTO wrapped in an ApiResponse
     */
    @PostMapping("/skills")
    public ResponseEntity<ApiResponse<ApiResponse.SkillDTO>> addSkillToCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AddSkillRequestDto dto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        ApiResponse.SkillDTO created = skillService.addSkillToCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Skill added successfully", created));
    }

    /**
     * Blocks a user from accessing the platform.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the user to block
     * @param statusUpdateDto the payload containing the reason for blocking
     * @return ResponseEntity indicating success
     */
    @PatchMapping("/users/{id}/block")
    public ResponseEntity<ApiResponse<String>> blockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                         @PathVariable Long id,
                                                         @RequestBody(required = false) UserStatusUpdateDto statusUpdateDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        String reason = (statusUpdateDto != null) ? statusUpdateDto.getReason() : null;
        userService.blockUser(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "User blocked successfully", null));
    }

    /**
     * Unblocks a previously blocked user.
     *
     * @param authHeader the authorization header containing the admin token
     * @param id the unique identifier of the user to unblock
     * @param statusUpdateDto the payload containing the reason for unblocking
     * @return ResponseEntity indicating success
     */
    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<ApiResponse<String>> unblockUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                           @PathVariable Long id,
                                                           @RequestBody(required = false) UserStatusUpdateDto statusUpdateDto) {
        authenticateAndGetEmail(authHeader); // Validate admin
        String reason = (statusUpdateDto != null) ? statusUpdateDto.getReason() : null;
        userService.unblockUser(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "User unblocked successfully", null));
    }

    /**
     * Retrieves all user queries (help/support tickets).
     *
     * @param authHeader the authorization header containing the admin token
     * @param status optional status to filter queries
     * @return ResponseEntity containing a list of UserQuery wrapped in an ApiResponse
     */
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

    /**
     * Replies to a specific user query.
     *
     * @param authHeader the authorization header containing the admin token
     * @param queryId the unique identifier of the query
     * @param replyDto the reply payload
     * @return ResponseEntity containing the updated UserQuery wrapped in an ApiResponse
     */
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
