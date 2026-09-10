package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.query.AdminQueryReplyDto;
import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.enums.QueryStatus;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.UserQueryRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.crimsonlogic.open_petal_backend.enums.QueryType;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryServiceImpl Tests")
class UserQueryServiceImplTest {

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Test
    @DisplayName("Should successfully submit a new support query")
    void givenValidUserAndRequest_whenSubmitQuery_thenSavesQueryWithPendingStatus() {
        User user = User.builder().id(1L).email("user@example.com").build();
        UserQueryRequestDto dto = new UserQueryRequestDto();
        dto.setQueryType(QueryType.SESSION_CONFLICT);
        dto.setSubject("Audio issue");
        dto.setDescription("Audio lag during live session");

        UserQuery savedQuery = UserQuery.builder().id(10L).user(user).queryType(QueryType.SESSION_CONFLICT).subject("Audio issue").status(QueryStatus.PENDING).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userQueryRepository.save(any(UserQuery.class))).willReturn(savedQuery);

        UserQuery result = userQueryService.submitQuery(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(QueryStatus.PENDING);
        verify(userQueryRepository).save(any(UserQuery.class));
    }

    @Test
    @DisplayName("Should throw RecordNotFoundException when admin replies to missing query")
    void givenInvalidQueryId_whenAdminReplyToQuery_thenThrowsException() {
        AdminQueryReplyDto replyDto = new AdminQueryReplyDto();
        replyDto.setStatus(QueryStatus.RESOLVED);
        replyDto.setAdminResponse("Resolved");

        given(userQueryRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userQueryService.adminReplyToQuery(99L, replyDto))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Query not found");
    }
}
