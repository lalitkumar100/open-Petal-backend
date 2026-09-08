package com.crimsonlogic.open_petal_backend.controller.user;

import com.crimsonlogic.open_petal_backend.dto.common.ApiResponse;
import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.service.LearningGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crimsonlogic.open_petal_backend.dto.skill.LearningGoalRequestDto;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing learning goals.
 * Provides endpoints to create, retrieve, update, and delete learning goals.
 */
@RestController
@RequestMapping("/api/v1/learning-goals")
public class LearningGoalController {

    private final LearningGoalService learningGoalService;

    @Autowired
    public LearningGoalController(LearningGoalService learningGoalService) {
        this.learningGoalService = learningGoalService;
    }

    /**
     * Creates a new learning goal for a user.
     *
     * @param requestDto the payload containing learning goal details
     * @return ResponseEntity containing the created LearningGoal
     */
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

    /**
     * Retrieves a specific learning goal by its ID.
     *
     * @param id the unique identifier of the learning goal
     * @return ResponseEntity containing the LearningGoal
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningGoal>> getLearningGoalById(@PathVariable Long id) {
        LearningGoal goal = learningGoalService.getLearningGoalById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal retrieved successfully", goal));
    }

    /**
     * Retrieves all learning goals associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return ResponseEntity containing a list of LearningGoals
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<LearningGoal>>> getLearningGoalsByUserId(@PathVariable Long userId) {
        List<LearningGoal> goals = learningGoalService.getLearningGoalsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goals retrieved successfully", goals));
    }

    /**
     * Updates an existing learning goal's target or current level.
     *
     * @param id the unique identifier of the learning goal to update
     * @param requestDto the payload containing updated details
     * @return ResponseEntity containing the updated LearningGoal
     */
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

    /**
     * Deletes a specific learning goal.
     *
     * @param id the unique identifier of the learning goal to delete
     * @return ResponseEntity indicating successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLearningGoal(@PathVariable Long id) {
        learningGoalService.deleteLearningGoal(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Learning goal deleted successfully", null));
    }

    /**
     * Updates the roadplan (AI-generated curriculum) for a learning goal.
     *
     * @param id the unique identifier of the learning goal
     * @param roadplan a map representation of the roadplan JSON
     * @return ResponseEntity containing the updated LearningGoal
     */
    @PatchMapping("/{id}/roadplan")
    public ResponseEntity<ApiResponse<LearningGoal>> updateRoadplan(
            @PathVariable Long id,
            @RequestBody Map<String, Object> roadplan) {
        
        LearningGoal updatedGoal = learningGoalService.updateRoadplan(id, roadplan);
        return ResponseEntity.ok(new ApiResponse<>(true, "Roadplan updated successfully", updatedGoal));
    }

    /**
     * Generates a roadplan (AI curriculum) for a learning goal.
     *
     * @param id the unique identifier of the learning goal
     * @return ResponseEntity containing the updated LearningGoal with roadplan
     */
    @PostMapping("/{id}/roadplan/generate")
    public ResponseEntity<ApiResponse<LearningGoal>> generateRoadplan(@PathVariable Long id) {
        LearningGoal updatedGoal = learningGoalService.generateRoadplan(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Roadplan generated successfully", updatedGoal));
    }
}
