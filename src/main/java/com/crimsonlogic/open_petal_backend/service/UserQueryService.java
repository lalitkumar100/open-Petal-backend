package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.query.AdminQueryReplyDto;
import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.enums.QueryStatus;

import java.util.List;


public interface UserQueryService {
    UserQuery submitQuery(Long userId, UserQueryRequestDto requestDto);
    UserQuery getQueryById(Long queryId);
    List<UserQuery> getQueriesByUser(Long userId);
    
    // Admin features
    List<UserQuery> getAllQueries();
    List<UserQuery> getQueriesByStatus(QueryStatus status);
    UserQuery adminReplyToQuery(Long queryId, AdminQueryReplyDto replyDto);
}
