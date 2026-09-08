package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.search.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.dto.search.UserSearchSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for search operations.
 */
public interface SearchService {
    /**
     * Search Users.
     */
    Page<UserSearchSummaryDto> searchUsers(String searchType, String query, Pageable pageable);
    /**
     * Get User Details.
     */
    UserDetailsDto getUserDetails(Long userId, Long loggedInUserId);
    
    /**
     * Recommend Mentors based on user's learning goals.
     */
    java.util.List<UserSearchSummaryDto> recommendMentors(Long loggedInUserId);

    /**
     * Recommend Barter Partners based on user's learning goals and skills offered.
     */
    java.util.List<UserSearchSummaryDto> recommendBarterPartners(Long loggedInUserId);
}
