package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import org.springframework.data.domain.Page;
import com.crimsonlogic.open_petal_backend.service.SkillCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing skill categories.
 * Provides endpoints for creating, retrieving, and updating categories.
 */
@RestController
@RequestMapping("/api/v1/skill-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SkillCategoryController {

    private final SkillCategoryService skillCategoryService;

    /**
     * Creates a new skill category.
     *
     * @param dto the request payload containing the category details
     * @return ResponseEntity containing the created SkillCategory
     */
    @PostMapping
    public ResponseEntity<SkillCategory> createCategory(@Valid @RequestBody SkillCategoryRequestDto dto) {
        SkillCategory created = skillCategoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves a paginated list of skill categories.
     *
     * @param page the page number to retrieve
     * @param size the number of records per page
     * @param searchQuery optional search query to filter categories
     * @return ResponseEntity containing a paginated list of categories wrapped in an ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SkillCategory>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String searchQuery) {
        Page<SkillCategory> categories = skillCategoryService.getAllCategories(page, size, searchQuery);
        return ResponseEntity.ok(new ApiResponse<>(true, "Skill categories retrieved successfully", categories));
    }

    /**
     * Retrieves a single skill category by its ID.
     *
     * @param id the unique identifier of the category
     * @return ResponseEntity containing the SkillCategory
     */
    @GetMapping("/{id}")
    public ResponseEntity<SkillCategory> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(skillCategoryService.getCategoryById(id));
    }

    /**
     * Updates an existing skill category.
     *
     * @param id the unique identifier of the category to update
     * @param dto the request payload containing the updated category details
     * @return ResponseEntity containing the updated SkillCategory
     */
    @PutMapping("/{id}")
    public ResponseEntity<SkillCategory> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody SkillCategoryRequestDto dto) {
        return ResponseEntity.ok(skillCategoryService.updateCategory(id, dto));
    }
}
