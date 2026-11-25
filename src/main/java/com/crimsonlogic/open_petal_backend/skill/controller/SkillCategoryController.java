package com.crimsonlogic.open_petal_backend.skill.controller;

import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillCategoryResponseDto;
import com.crimsonlogic.open_petal_backend.skill.service.SkillCategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skill-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SkillCategoryController {

    private final MessageService messageService;

    private final SkillCategoryService skillCategoryService;

    // 1. Create a new skill category
    @PostMapping
    public ResponseEntity<ApiResponse<SkillCategoryResponseDto>> createCategory(@Valid @RequestBody SkillCategoryRequestDto dto) {
        SkillCategoryResponseDto created = SkillCategoryResponseDto.from(skillCategoryService.createCategory(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                MessageCodes.SKILL_CATEGORY_CREATED,
                messageService.getMessage(MessageCodes.SKILL_CATEGORY_CREATED),
                created));
    }

    // 2. Get all skill categories (Paginated)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SkillCategoryResponseDto>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String searchQuery) {
        Page<SkillCategoryResponseDto> categories = skillCategoryService.getAllCategories(page, size, searchQuery)
                .map(SkillCategoryResponseDto::from);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.SKILL_CATEGORIES_RETRIEVED,
                messageService.getMessage(MessageCodes.SKILL_CATEGORIES_RETRIEVED),
                categories));
    }

    // 3. Get single category by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillCategoryResponseDto>> getCategoryById(@PathVariable Long id) {
        SkillCategoryResponseDto category = SkillCategoryResponseDto.from(skillCategoryService.getCategoryById(id));
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.SKILL_CATEGORY_RETRIEVED,
                messageService.getMessage(MessageCodes.SKILL_CATEGORY_RETRIEVED),
                category));
    }

    // 4. Update an existing skill category
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillCategoryResponseDto>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody SkillCategoryRequestDto dto) {
        SkillCategoryResponseDto category = SkillCategoryResponseDto.from(skillCategoryService.updateCategory(id, dto));
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.SKILL_CATEGORY_UPDATED,
                messageService.getMessage(MessageCodes.SKILL_CATEGORY_UPDATED),
                category));
    }
}
