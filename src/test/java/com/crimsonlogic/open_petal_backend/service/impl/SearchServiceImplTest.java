package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.search.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.ConnectionRequestRepository;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchServiceImpl Tests")
class SearchServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConnectionRequestRepository connectionRequestRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    @DisplayName("Should successfully retrieve user details for search preview")
    void givenValidUserId_whenGetUserProfileDetails_thenReturnsDetailsDto() {
        User user = User.builder()
                .id(40L)
                .firstName("Keerthi")
                .lastName("Naik")
                .email("keerthi@example.com")
                .build();

        given(userRepository.findById(40L)).willReturn(Optional.of(user));

        UserDetailsDto result = searchService.getUserDetails(40L, null);

        assertThat(result).isNotNull();
        assertThat(result.getUserInfo().getFirstName()).isEqualTo("Keerthi");
        assertThat(result.getUserInfo().getUserId()).isEqualTo(40L);
    }

    @Test
    @DisplayName("Should throw RecordNotFoundException when searched user does not exist")
    void givenInvalidUserId_whenGetUserProfileDetails_thenThrowsRecordNotFoundException() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> searchService.getUserDetails(999L, null))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
