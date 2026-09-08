package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.search.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.dto.search.UserSearchSummaryDto;
import com.crimsonlogic.open_petal_backend.service.SearchService;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for search operations.
 * Provides endpoints for searching users and retrieving public user details.
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;
    private final AuthService authService;
    private final UserService userService;

    /**
     * Searches for users based on name, email, or skill.
     *
     * @param searchType the type of search (e.g., user, skill)
     * @param query the search query string
     * @param page the page number to retrieve
     * @param size the number of records per page
     * @return ResponseEntity containing a paginated list of UserSearchSummaryDto
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
        return ResponseEntity.ok(new ApiResponse<>(true, "Search results fetched successfully", result));
    }

    /**
     * Retrieves detailed profile information for a specific user.
     *
     * @param id the unique identifier of the user to retrieve
     * @param authHeader optional authorization header for authenticated viewing context
     * @return ResponseEntity containing UserDetailsDto
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
            } catch (Exception e) {
                // Ignore auth errors for public viewing, just treat as not logged in
            }
        }

        UserDetailsDto userDetails = searchService.getUserDetails(id, loggedInUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User details fetched successfully", userDetails));
    }
    
    private String authenticateAndGetEmail(String authHeader) {
        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }
        return authService.getEmailFromToken(token);
    }
}
