package com.crimsonlogic.open_petal_backend.skill.controller;

import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.skill.dto.LearningGoalRequestDto;
import com.crimsonlogic.open_petal_backend.skill.dto.LearningGoalResponseDto;
import com.crimsonlogic.open_petal_backend.skill.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.skill.service.LearningGoalService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning-goals")
public class LearningGoalController {

    private final MessageService messageService;

    private final LearningGoalService learningGoalService;

    @Autowired
    public LearningGoalController(LearningGoalService learningGoalService,
                           MessageService messageService) {
        this.messageService = messageService;
        this.learningGoalService = learningGoalService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LearningGoalResponseDto>> createLearningGoal(@Valid @RequestBody LearningGoalRequestDto requestDto) {
        LearningGoal goal = learningGoalService.createLearningGoal(
                requestDto.getUserId(),
                requestDto.getSkillId(),
                requestDto.getCurrentLevel(),
                requestDto.getTargetLevel()
        );
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.LEARNING_GOAL_CREATED, messageService.getMessage(MessageCodes.LEARNING_GOAL_CREATED), toResponse(goal)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningGoalResponseDto>> getLearningGoalById(@PathVariable Long id) {
        LearningGoal goal = learningGoalService.getLearningGoalById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.LEARNING_GOAL_RETRIEVED, messageService.getMessage(MessageCodes.LEARNING_GOAL_RETRIEVED), toResponse(goal)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<LearningGoalResponseDto>>> getLearningGoalsByUserId(@PathVariable Long userId) {
        List<LearningGoalResponseDto> goals = learningGoalService.getLearningGoalsByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.LEARNING_GOALS_RETRIEVED, messageService.getMessage(MessageCodes.LEARNING_GOALS_RETRIEVED), goals));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LearningGoalResponseDto>> updateLearningGoal(
            @PathVariable Long id,
            @Valid @RequestBody LearningGoalRequestDto requestDto) {
        LearningGoal updatedGoal = learningGoalService.updateLearningGoal(
                id,
                requestDto.getCurrentLevel(),
                requestDto.getTargetLevel()
        );
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.LEARNING_GOAL_UPDATED, messageService.getMessage(MessageCodes.LEARNING_GOAL_UPDATED), toResponse(updatedGoal)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLearningGoal(@PathVariable Long id) {
        learningGoalService.deleteLearningGoal(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.LEARNING_GOAL_DELETED, messageService.getMessage(MessageCodes.LEARNING_GOAL_DELETED), null));
    }

    @PatchMapping("/{id}/roadplan")
    public ResponseEntity<ApiResponse<LearningGoalResponseDto>> updateRoadplan(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, Object> roadplan) {
        
        LearningGoal updatedGoal = learningGoalService.updateRoadplan(id, roadplan);
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.LEARNING_GOAL_ROADPLAN_UPDATED, messageService.getMessage(MessageCodes.LEARNING_GOAL_ROADPLAN_UPDATED), toResponse(updatedGoal)));
    }

    private LearningGoalResponseDto toResponse(LearningGoal learningGoal) {
        return LearningGoalResponseDto.builder()
                .id(learningGoal.getId())
                .skillId(learningGoal.getSkill().getId())
                .skillName(learningGoal.getSkill().getName())
                .currentLevel(learningGoal.getCurrentLevel())
                .targetLevel(learningGoal.getTargetLevel())
                .isActive(!learningGoal.getIsDeleted())
                .roadplan(learningGoal.getRoadplan())
                .build();
    }
}
