package com.crimsonlogic.open_petal_backend.search.controller;

import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.common.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.search.dto.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.search.dto.UserSearchSummaryDto;
import com.crimsonlogic.open_petal_backend.search.service.SearchService;
import com.crimsonlogic.open_petal_backend.user.entity.User;
import com.crimsonlogic.open_petal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchController {

    private final MessageService messageService;

    private final SearchService searchService;
    private final AuthService authService;
    private final UserService userService;

    /**
     * Search users by user name/email or skill name
     * GET /api/v1/search/users?searchType=user&query=john&page=0&size=10
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserSearchSummaryDto>>> searchUsers(
            @RequestParam(defaultValue = "user") String searchType,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserSearchSummaryDto> result = searchService.searchUsers(searchType, query, pageable);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SEARCH_RESULTS_RETRIEVED, messageService.getMessage(MessageCodes.SEARCH_RESULTS_RETRIEVED), result));
    }

    /**
     * Get user details for user profile / details page
     * GET /api/v1/search/users/1/details
     */
    @GetMapping("/users/{id}/details")
    public ResponseEntity<ApiResponse<UserDetailsDto>> getUserDetails(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long loggedInUserId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String email = authenticateAndGetEmail(authHeader);
                User user = userService.getUserByEmail(email).orElse(null);
                if (user != null) {
                    loggedInUserId = user.getId();
                }
            } catch (AuthenticationException ignored) {
                // This profile endpoint remains publicly viewable when optional authentication is invalid.
            }
        }

        UserDetailsDto userDetails = searchService.getUserDetails(id, loggedInUserId);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SEARCH_USER_DETAILS_RETRIEVED, messageService.getMessage(MessageCodes.SEARCH_USER_DETAILS_RETRIEVED), userDetails));
    }
    
    private String authenticateAndGetEmail(String authHeader) {
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }
        return authService.getEmailFromToken(token);
    }
}
