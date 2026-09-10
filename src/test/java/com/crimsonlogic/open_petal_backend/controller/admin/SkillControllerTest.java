package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.skill.SkillDetailResponseDto;
import com.crimsonlogic.open_petal_backend.service.SkillService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("SkillController Integration Tests")
class SkillControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing SkillControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed SkillControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skillController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    /**
     * Purpose: Verifies retrieving all system skills list.
     * Preconditions: Skills exist in repository catalog.
     * Expected Behavior: Returns HTTP 200 OK with list of skills.
     */
    @Test
    @DisplayName("Should return 200 OK with system skills list")
    void whenGetAllSkills_thenReturns200AndSkillList() throws Exception {
        ApiResponse.SkillDTO dto = ApiResponse.SkillDTO.builder().id(1L).name("Java").build();
        given(skillService.getAllSkills()).willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Java"));
    }

    /**
     * Purpose: Verifies fetching paginated skill details.
     * Preconditions: Pagination parameters provided.
     * Expected Behavior: Returns HTTP 200 OK with paginated skill details.
     */
    @Test
    @DisplayName("Should return 200 OK with paginated skill details")
    void whenGetAllSkillDetails_thenReturns200AndSkillPage() throws Exception {
        SkillDetailResponseDto dto = SkillDetailResponseDto.builder()
                .id(1L)
                .name("Python")
                .categoryName("Data Science")
                .build();

        given(skillService.getAllSkillDetails(anyInt(), anyInt(), anyString()))
                .willReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/skills/details/page?page=0&size=10&search=Python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Python"));
    }

    /**
     * Purpose: Verifies fetching empty search result page gracefully.
     * Preconditions: Non-matching search query provided.
     * Expected Behavior: Returns HTTP 200 OK with empty content page.
     */
    @Test
    @DisplayName("Should return 200 OK with empty page for non-matching search")
    void givenNoMatch_whenGetAllSkillDetails_thenReturns200AndEmptyPage() throws Exception {
        given(skillService.getAllSkillDetails(anyInt(), anyInt(), anyString()))
                .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/api/v1/skills/details/page?search=nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isEmpty());
    }
}
