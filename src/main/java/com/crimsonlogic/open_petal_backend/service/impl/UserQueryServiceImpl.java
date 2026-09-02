package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.query.AdminQueryReplyDto;
import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.enums.QueryStatus;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.UserQueryRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserQueryServiceImpl(UserQueryRepository userQueryRepository, UserRepository userRepository) {
        this.userQueryRepository = userQueryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserQuery submitQuery(Long userId, UserQueryRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));

        UserQuery userQuery = UserQuery.builder()
                .user(user)
                .queryType(requestDto.getQueryType())
                .subject(requestDto.getSubject())
                .description(requestDto.getDescription())
                .status(QueryStatus.PENDING) // Explicitly setting status
                .build();

        return userQueryRepository.save(userQuery);
    }

    @Override
    public UserQuery getQueryById(Long queryId) {
        return userQueryRepository.findById(queryId)
                .orElseThrow(() -> new RecordNotFoundException("Query not found with id " + queryId));
    }

    @Override
    public List<UserQuery> getQueriesByUser(Long userId) {
        // Validate user existence optional but good practice
        userRepository.findById(userId)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
                
        return userQueryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<UserQuery> getAllQueries() {
        return userQueryRepository.findAll();
    }

    @Override
    public List<UserQuery> getQueriesByStatus(QueryStatus status) {
        return userQueryRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public UserQuery adminReplyToQuery(Long queryId, AdminQueryReplyDto replyDto) {
        UserQuery userQuery = userQueryRepository.findById(queryId)
                .orElseThrow(() -> new RecordNotFoundException("Query not found with id " + queryId));

        userQuery.setStatus(replyDto.getStatus());
        if (replyDto.getAdminResponse() != null) {
            userQuery.setAdminResponse(replyDto.getAdminResponse());
        }

        return userQueryRepository.save(userQuery);
    }
}
