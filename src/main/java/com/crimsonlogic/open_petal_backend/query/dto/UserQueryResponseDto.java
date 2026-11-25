package com.crimsonlogic.open_petal_backend.query.dto;

import com.crimsonlogic.open_petal_backend.query.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.query.enums.QueryStatus;
import com.crimsonlogic.open_petal_backend.query.enums.QueryType;
import java.time.LocalDateTime;

public record UserQueryResponseDto(
        Long id,
        Long userId,
        QueryType queryType,
        QueryStatus status,
        String subject,
        String description,
        String adminResponse,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserQueryResponseDto from(UserQuery query) {
        return new UserQueryResponseDto(
                query.getId(),
                query.getUser() == null ? null : query.getUser().getId(),
                query.getQueryType(),
                query.getStatus(),
                query.getSubject(),
                query.getDescription(),
                query.getAdminResponse(),
                query.getCreatedAt(),
                query.getUpdatedAt());
    }
}
