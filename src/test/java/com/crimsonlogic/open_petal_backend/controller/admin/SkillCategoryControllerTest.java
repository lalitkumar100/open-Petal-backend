package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.service.SkillCategoryService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("SkillCategoryController Integration Tests")
class SkillCategoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SkillCategoryService skillCategoryService;

    @InjectMocks
    private SkillCategoryController skillCategoryController;

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing SkillCategoryControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed SkillCategoryControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skillCategoryController).build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Purpose: Verifies standard POST creation behavior.
     * Preconditions: Valid SkillCategoryRequestDto provided.
     * Expected Behavior: Returns 201 Created with saved category.
     */
    @Test
    @DisplayName("Should return 201 Created on category creation")
    void givenValidDto_whenCreateCategory_thenReturns201() throws Exception {
        // Arrange
        SkillCategoryRequestDto request = new SkillCategoryRequestDto();
        request.setName("Technology");

        SkillCategory responseCategory = new SkillCategory();
        responseCategory.setId(1L);
        responseCategory.setName("Technology");

        given(skillCategoryService.createCategory(any(SkillCategoryRequestDto.class))).willReturn(responseCategory);

        // Act & Assert
        mockMvc.perform(post("/api/v1/skill-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    /**
     * Purpose: Tests validation boundaries on category creation.
     * Preconditions: DTO provided with blank name.
     * Expected Behavior: Returns 400 Bad Request.
     */
    @Test
    @DisplayName("Should return 400 Bad Request on invalid category creation payload")
    void givenInvalidDto_whenCreateCategory_thenReturns400() throws Exception {
        // Arrange
        SkillCategoryRequestDto request = new SkillCategoryRequestDto();
        request.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/skill-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Purpose: Tests pagination parameters mapping.
     * Preconditions: Valid GET request with params.
     * Expected Behavior: Returns 200 OK with paginated ApiResponse.
     */
    @Test
    @DisplayName("Should return paginated categories successfully")
    void givenValidPage_whenGetAllCategories_thenReturnsPaginatedResponse() throws Exception {
        // Arrange
        given(skillCategoryService.getAllCategories(anyInt(), anyInt(), any())).willReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/skill-categories?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Purpose: Tests single item retrieval.
     * Preconditions: Category exists.
     * Expected Behavior: Returns 200 OK and category payload.
     */
    @Test
    @DisplayName("Should return single category by ID")
    void givenValidId_whenGetCategoryById_thenReturns200() throws Exception {
        // Arrange
        SkillCategory category = new SkillCategory();
        category.setId(1L);
        category.setName("Art");

        given(skillCategoryService.getCategoryById(1L)).willReturn(category);

        // Act & Assert
        mockMvc.perform(get("/api/v1/skill-categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Art"));
    }
}
