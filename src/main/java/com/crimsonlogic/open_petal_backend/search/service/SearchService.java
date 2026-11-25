package com.crimsonlogic.open_petal_backend.search.service;

import com.crimsonlogic.open_petal_backend.search.dto.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.search.dto.UserSearchSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<UserSearchSummaryDto> searchUsers(String searchType, String query, Pageable pageable);
    UserDetailsDto getUserDetails(Long userId, Long loggedInUserId);
}
