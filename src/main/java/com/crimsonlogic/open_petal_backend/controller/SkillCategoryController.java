package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.service.SkillCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skill-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SkillCategoryController {

    private final SkillCategoryService skillCategoryService;

    // 1. Create a new skill category
    @PostMapping
    public ResponseEntity<SkillCategory> createCategory(@Valid @RequestBody SkillCategoryRequestDto dto) {
        SkillCategory created = skillCategoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 2. Get all skill categories
    @GetMapping
    public ResponseEntity<List<SkillCategory>> getAllCategories() {
        return ResponseEntity.ok(skillCategoryService.getAllCategories());
    }

    // 3. Get single category by ID
    @GetMapping("/{id}")
    public ResponseEntity<SkillCategory> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(skillCategoryService.getCategoryById(id));
    }

    // 4. Update an existing skill category
    @PutMapping("/{id}")
    public ResponseEntity<SkillCategory> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody SkillCategoryRequestDto dto) {
        return ResponseEntity.ok(skillCategoryService.updateCategory(id, dto));
    }
}