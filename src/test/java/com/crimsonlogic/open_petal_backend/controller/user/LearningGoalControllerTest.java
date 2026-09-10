package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.skill.LearningGoalRequestDto;
import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("LearningGoalController Integration Tests")
class LearningGoalControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LearningGoalService learningGoalService;

    @InjectMocks
    private LearningGoalController learningGoalController;

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing LearningGoalControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed LearningGoalControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(learningGoalController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    /**
     * Purpose: Verifies adding a learning goal for a user.
     * Preconditions: Valid LearningGoalRequestDto provided.
     * Expected Behavior: Returns HTTP 200 OK with saved LearningGoal.
     */
    @Test
    @DisplayName("Should return 200 OK on creating a learning goal")
    void givenValidGoalRequest_whenCreateLearningGoal_thenReturns200AndGoal() throws Exception {
        // Arrange
        LearningGoalRequestDto request = new LearningGoalRequestDto();
        request.setUserId(1L);
        request.setSkillId(10L);
        request.setCurrentLevel(SkillLevel.BEGINNER);
        request.setTargetLevel(SkillLevel.EXPERT);

        Skill skill = new Skill();
        skill.setId(10L);
        skill.setName("Python");

        LearningGoal goal = LearningGoal.builder()
                .id(1L)
                .skill(skill)
                .targetLevel(SkillLevel.EXPERT)
                .build();

        given(learningGoalService.createLearningGoal(eq(1L), eq(10L), eq(SkillLevel.BEGINNER), eq(SkillLevel.EXPERT))).willReturn(goal);

        // Act & Assert
        mockMvc.perform(post("/api/v1/learning-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * Purpose: Verifies retrieving a learning goal by ID.
     * Preconditions: Goal ID exists.
     * Expected Behavior: Returns HTTP 200 OK with LearningGoal.
     */
    @Test
    @DisplayName("Should return 200 OK with learning goal by ID")
    void givenGoalId_whenGetLearningGoalById_thenReturns200AndGoal() throws Exception {
        // Arrange
        LearningGoal goal = LearningGoal.builder()
                .id(1L)
                .targetLevel(SkillLevel.EXPERT)
                .build();

        given(learningGoalService.getLearningGoalById(1L)).willReturn(goal);

        // Act & Assert
        mockMvc.perform(get("/api/v1/learning-goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * Purpose: Verifies deleting a learning goal by ID.
     * Preconditions: Valid goal ID provided.
     * Expected Behavior: Returns HTTP 200 OK with deletion message.
     */
    @Test
    @DisplayName("Should return 200 OK on deleting a learning goal")
    void givenGoalId_whenDeleteGoal_thenReturns200Success() throws Exception {
        // Arrange
        doNothing().when(learningGoalService).deleteLearningGoal(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/learning-goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Learning goal deleted successfully"));
    }
}
