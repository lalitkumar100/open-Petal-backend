package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.query.AdminQueryReplyDto;
import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.enums.QueryStatus;

import java.util.List;


/**
 * Service interface for user query operations.
 */
public interface UserQueryService {
    /**
     * Submit Query.
     */
    UserQuery submitQuery(Long userId, UserQueryRequestDto requestDto);
    /**
     * Get Query By Id.
     */
    UserQuery getQueryById(Long queryId);
    /**
     * Get Queries By User.
     */
    List<UserQuery> getQueriesByUser(Long userId);
    
    // Admin features
    /**
     * Get All Queries.
     */
    List<UserQuery> getAllQueries();
    /**
     * Get Queries By Status.
     */
    List<UserQuery> getQueriesByStatus(QueryStatus status);
    /**
     * Admin Reply To Query.
     */
    UserQuery adminReplyToQuery(Long queryId, AdminQueryReplyDto replyDto);
}
