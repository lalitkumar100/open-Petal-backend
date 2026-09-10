package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.skill.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;
import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.repository.UserSkillRepository;
import com.crimsonlogic.open_petal_backend.service.GeminiAiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserSkillServiceImpl Tests")
class UserSkillServiceImplTest {

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GeminiAiClient geminiAiClient;

    @InjectMocks
    private UserSkillServiceImpl userSkillService;

    @Test
    @DisplayName("Should successfully add a teaching skill to user profile")
    void givenValidUserAndSkill_whenAddSkillToUser_thenSavesUserSkill() {
        User user = User.builder().id(1L).email("user@example.com").build();
        Skill skill = Skill.builder().id(3L).name("Python").build();

        CreateUserSkillDto dto = new CreateUserSkillDto();
        dto.setSkillId(3L);
        dto.setSkillLevel(SkillLevel.INTERMEDIATE);
        dto.setExperienceYears(2);
        dto.setTeachingMode(TeachingMode.ONLINE);

        UserSkill savedSkill = UserSkill.builder().id(37L).user(user).skill(skill).skillLevel(SkillLevel.INTERMEDIATE).build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(skillRepository.findById(3L)).willReturn(Optional.of(skill));
        given(userSkillRepository.existsByUserIdAndSkillId(1L, 3L)).willReturn(false);
        given(userSkillRepository.save(any(UserSkill.class))).willReturn(savedSkill);

        UserSkill result = userSkillService.addSkillToUser(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(37L);
        verify(userSkillRepository).save(any(UserSkill.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when skill already exists in user profile")
    void givenExistingSkillInProfile_whenAddSkillToUser_thenThrowsException() {
        User user = User.builder().id(1L).build();
        Skill skill = Skill.builder().id(3L).build();

        CreateUserSkillDto dto = new CreateUserSkillDto();
        dto.setSkillId(3L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(skillRepository.findById(3L)).willReturn(Optional.of(skill));
        given(userSkillRepository.existsByUserIdAndSkillId(1L, 3L)).willReturn(true);

        assertThatThrownBy(() -> userSkillService.addSkillToUser(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User has already listed this skill");
    }

    @Test
    @DisplayName("Should throw RecordNotFoundException when skill offering is missing for verification")
    void givenMissingUserSkill_whenGetUserSkillDetails_thenThrowsRecordNotFoundException() {
        given(userSkillRepository.findByUserIdAndSkillId(40L, 99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userSkillService.getUserSkillDetails(40L, 99L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Skill offering not found");
    }
}
