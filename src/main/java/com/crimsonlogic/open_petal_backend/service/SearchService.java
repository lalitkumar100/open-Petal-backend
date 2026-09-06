package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.search.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.dto.search.UserSearchSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<UserSearchSummaryDto> searchUsers(String searchType, String query, Pageable pageable);
    UserDetailsDto getUserDetails(Long userId, Long loggedInUserId);
}
