package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing skills.
 * Handles API requests related to retrieving system skills.
 */
@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * Retrieves all available skills in the system.
     *
     * @return ResponseEntity containing a list of skills wrapped in an ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiResponse.SkillDTO>>> getAllSkills() {
        List<ApiResponse.SkillDTO> skills = skillService.getAllSkills();
        return ResponseEntity.ok(new ApiResponse<>(true, "Skills retrieved successfully", skills));
    }
}
