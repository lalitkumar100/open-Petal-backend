package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.search.UserDetailsDto;
import com.crimsonlogic.open_petal_backend.dto.search.UserSearchSummaryDto;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.RoleType;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.SearchService;
import com.crimsonlogic.open_petal_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchController Integration Tests")
class SearchControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SearchService searchService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SearchController searchController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing SearchControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed SearchControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private void mockUserAuth() {
        given(authService.validateToken("valid.user.token")).willReturn(true);
        given(authService.getEmailFromToken("valid.user.token")).willReturn("user@openpetal.com");

        Login login = new Login();
        login.setRole(RoleType.ROLE_USER);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@openpetal.com");
        user.setLogin(login);

        given(userService.getUserByEmail("user@openpetal.com")).willReturn(Optional.of(user));
    }

    /**
     * Purpose: Verifies searching users with keyword query and filters.
     * Preconditions: Valid query string provided.
     * Expected Behavior: Returns HTTP 200 OK with paginated search summaries.
     */
    @Test
    @DisplayName("Should return 200 OK on user search query")
    void whenSearchUsers_thenReturns200AndUserPage() throws Exception {
        UserSearchSummaryDto summary = UserSearchSummaryDto.builder()
                .userId(10L)
                .firstName("Carol")
                .lastName("Danvers")
                .build();

        given(searchService.searchUsers(any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/search/users?query=Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].firstName").value("Carol"));
    }

    /**
     * Purpose: Verifies fetching full public profile details for a searched user.
     * Preconditions: Searched user ID exists.
     * Expected Behavior: Returns HTTP 200 OK with UserDetailsDto.
     */
    @Test
    @DisplayName("Should return 200 OK with user details for search preview")
    void givenUserId_whenGetUserDetails_thenReturns200AndDetails() throws Exception {
        UserDetailsDto details = UserDetailsDto.builder()
                .userInfo(UserDetailsDto.UserInfoDto.builder().userId(10L).firstName("Carol").build())
                .build();

        given(searchService.getUserDetails(eq(10L), any())).willReturn(details);

        mockMvc.perform(get("/api/v1/search/users/10/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userInfo.firstName").value("Carol"));
    }

    /**
     * Purpose: Verifies mentor recommendations for a logged-in user.
     * Preconditions: Valid Authorization header provided.
     * Expected Behavior: Returns HTTP 200 OK with recommended mentors list.
     */
    @Test
    @DisplayName("Should return 200 OK on mentor recommendations request")
    void whenRecommendMentors_thenReturns200AndMentorsList() throws Exception {
        mockUserAuth();
        UserSearchSummaryDto mentor = UserSearchSummaryDto.builder()
                .userId(20L)
                .firstName("Dave")
                .build();

        given(searchService.recommendMentors(1L)).willReturn(List.of(mentor));

        mockMvc.perform(get("/api/v1/search/recommendations/mentors")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].firstName").value("Dave"));
    }
}
