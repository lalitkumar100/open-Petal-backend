package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LearningGoalRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.service.GeminiAiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LearningGoalServiceImpl Tests")
class LearningGoalServiceImplTest {

    @Mock
    private LearningGoalRepository learningGoalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GeminiAiClient geminiAiClient;

    @InjectMocks
    private LearningGoalServiceImpl learningGoalService;

    @Test
    @DisplayName("Should successfully create a learning goal")
    void givenValidUserAndSkill_whenCreateLearningGoal_thenReturnsSavedGoal() {
        User user = User.builder().id(1L).email("user@example.com").build();
        Skill skill = Skill.builder().id(10L).name("Java").build();
        LearningGoal savedGoal = LearningGoal.builder().id(100L).user(user).skill(skill).currentLevel(SkillLevel.BEGINNER).targetLevel(SkillLevel.ADVANCED).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(skillRepository.findById(10L)).willReturn(Optional.of(skill));
        given(learningGoalRepository.findByUserIdAndSkillId(1L, 10L)).willReturn(Optional.empty());
        given(learningGoalRepository.save(any(LearningGoal.class))).willReturn(savedGoal);

        LearningGoal result = learningGoalService.createLearningGoal(1L, 10L, SkillLevel.BEGINNER, SkillLevel.ADVANCED);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(learningGoalRepository).save(any(LearningGoal.class));
    }

    @Test
    @DisplayName("Should throw RecordNotFoundException when user is missing")
    void givenInvalidUser_whenCreateLearningGoal_thenThrowsException() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> learningGoalService.createLearningGoal(99L, 10L, SkillLevel.BEGINNER, SkillLevel.ADVANCED))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should throw CustomException CONFLICT when active goal exists")
    void givenExistingActiveGoal_whenCreateLearningGoal_thenThrowsConflict() {
        User user = User.builder().id(1L).build();
        Skill skill = Skill.builder().id(10L).build();
        LearningGoal existingGoal = LearningGoal.builder().id(100L).isDeleted(false).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(skillRepository.findById(10L)).willReturn(Optional.of(skill));
        given(learningGoalRepository.findByUserIdAndSkillId(1L, 10L)).willReturn(Optional.of(existingGoal));

        assertThatThrownBy(() -> learningGoalService.createLearningGoal(1L, 10L, SkillLevel.BEGINNER, SkillLevel.ADVANCED))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);
    }
}
