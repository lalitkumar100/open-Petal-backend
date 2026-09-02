package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.dto.query.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/queries")
public class UserQueryController {

    private final UserQueryService userQueryService;

    @Autowired
    public UserQueryController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserQuery>> submitQuery(
            @Valid @RequestBody UserQueryRequestDto requestDto, Long userId) {
        UserQuery createdQuery = userQueryService.submitQuery(userId, requestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Query submitted successfully", createdQuery));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserQuery>> getQueryById(@PathVariable Long id) {
        UserQuery query = userQueryService.getQueryById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Query retrieved successfully", query));
    }
}
