package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.enums.QueryStatus;
import com.crimsonlogic.open_petal_backend.enums.QueryType;
import com.crimsonlogic.open_petal_backend.enums.RoleType;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryController Integration Tests")
class UserQueryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserQueryController userQueryController;

    private static final String AUTH_HEADER = "Bearer valid.user.token";

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing UserQueryControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed UserQueryControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userQueryController).build();
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
     * Purpose: Verifies submitting a support query ticket.
     * Preconditions: Valid UserQueryRequestDto provided.
     * Expected Behavior: Returns HTTP 200 OK with saved UserQuery.
     */
    @Test
    @DisplayName("Should return 200 OK on support query submission")
    void givenValidQueryRequest_whenSubmitQuery_thenReturns201() throws Exception {
        mockUserAuth();
        UserQueryRequestDto request = new UserQueryRequestDto();
        request.setQueryType(QueryType.GENERAL_ADVICE);
        request.setSubject("Platform suggestion");
        request.setDescription("Add dark mode support");

        UserQuery queryEntity = UserQuery.builder()
                .id(10L)
                .status(QueryStatus.PENDING)
                .build();

        given(userQueryService.submitQuery(eq(1L), any(UserQueryRequestDto.class))).willReturn(queryEntity);

        mockMvc.perform(post("/api/v1/queries")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10));
    }

    /**
     * Purpose: Verifies retrieving support queries submitted by user.
     * Preconditions: Authorization header provided.
     * Expected Behavior: Returns HTTP 200 OK with list of submitted queries.
     */
    @Test
    @DisplayName("Should return 200 OK with user's submitted queries list")
    void whenGetMyQueries_thenReturns200AndQueriesList() throws Exception {
        mockUserAuth();
        UserQuery queryEntity = UserQuery.builder()
                .id(10L)
                .subject("Platform suggestion")
                .status(QueryStatus.PENDING)
                .build();

        given(userQueryService.getQueriesByUser(1L)).willReturn(List.of(queryEntity));

        mockMvc.perform(get("/api/v1/queries")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(10));
    }

    /**
     * Purpose: Verifies fetching query by ID.
     * Preconditions: Query ID exists.
     * Expected Behavior: Returns HTTP 200 OK with UserQuery entity.
     */
    @Test
    @DisplayName("Should return 200 OK on fetching query by ID")
    void givenQueryId_whenGetQueryById_thenReturns200AndQuery() throws Exception {
        UserQuery queryEntity = UserQuery.builder()
                .id(10L)
                .subject("Platform suggestion")
                .status(QueryStatus.PENDING)
                .build();

        given(userQueryService.getQueryById(10L)).willReturn(queryEntity);

        mockMvc.perform(get("/api/v1/queries/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10));
    }
}
