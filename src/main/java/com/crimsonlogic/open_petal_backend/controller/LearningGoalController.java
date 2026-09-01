package com.crimsonlogic.open_petal_backend.controller;

import com.crimsonlogic.open_petal_backend.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crimsonlogic.open_petal_backend.dto.LearningGoalRequestDto;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/learning-goals")
public class LearningGoalController {

    private final LearningGoalService learningGoalService;

    @Autowired
    public LearningGoalController(LearningGoalService learningGoalService) {
        this.learningGoalService = learningGoalService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LearningGoal>> createLearningGoal(@RequestBody LearningGoalRequestDto requestDto) {
        LearningGoal goal = learningGoalService.createLearningGoal(
                requestDto.getUserId(),
                requestDto.getSkillId(),
                requestDto.getCurrentLevel(),
                requestDto.getTargetLevel()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal created successfully", goal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningGoal>> getLearningGoalById(@PathVariable Long id) {
        LearningGoal goal = learningGoalService.getLearningGoalById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal retrieved successfully", goal));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<LearningGoal>>> getLearningGoalsByUserId(@PathVariable Long userId) {
        List<LearningGoal> goals = learningGoalService.getLearningGoalsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goals retrieved successfully", goals));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningGoal>> updateLearningGoal(
            @PathVariable Long id,
            @RequestBody LearningGoalRequestDto requestDto) {
        LearningGoal updatedGoal = learningGoalService.updateLearningGoal(
                id,
                requestDto.getCurrentLevel(),
                requestDto.getTargetLevel()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal updated successfully", updatedGoal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLearningGoal(@PathVariable Long id) {
        learningGoalService.deleteLearningGoal(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal deleted successfully", null));
    }

    @PatchMapping("/{id}/roadplan")
    public ResponseEntity<ApiResponse<LearningGoal>> updateRoadplan(
            @PathVariable Long id,
            @RequestBody Map<String, Object> roadplan) {
        
        LearningGoal updatedGoal = learningGoalService.updateRoadplan(id, roadplan);
        return ResponseEntity.ok(new ApiResponse<>(true, "Roadplan updated successfully", updatedGoal));
    }
}
