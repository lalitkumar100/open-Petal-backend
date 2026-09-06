package com.crimsonlogic.open_petal_backend.seeder;

import com.crimsonlogic.open_petal_backend.entity.LearningGoal;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;
import com.crimsonlogic.open_petal_backend.enums.SkillLevel;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import com.crimsonlogic.open_petal_backend.repository.LearningGoalRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoAddSkillToUserSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoAddSkillToUserSeeder.class);

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final LearningGoalRepository learningGoalRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Checking if user skills and learning goals seed is needed...");

        List<User> users = userRepository.findAll();
        List<Skill> skills = skillRepository.findAll();

        if (users.isEmpty() || skills.isEmpty()) {
            logger.info("No users or skills found in database. Skipping demo skills assignment.");
            return;
        }

        int totalSkills = skills.size();
        logger.info("Found {} users and {} skills in database.", users.size(), totalSkills);

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            // Select 2 skills for teaching (tech skills) and 2 for learning
            Skill teachSkill1 = skills.get((i * 4) % totalSkills);
            Skill teachSkill2 = skills.get((i * 4 + 1) % totalSkills);

            Skill learnSkill1 = skills.get((i * 4 + 2) % totalSkills);
            Skill learnSkill2 = skills.get((i * 4 + 3) % totalSkills);

            // 1. Add Teaching Skills if not exists
            addTeachingSkill(user, teachSkill1, SkillLevel.EXPERT, 4);
            addTeachingSkill(user, teachSkill2, SkillLevel.INTERMEDIATE, 2);

            // 2. Add Learning Goals if not exists
            addLearningGoal(user, learnSkill1, SkillLevel.BEGINNER, SkillLevel.ADVANCED);
            addLearningGoal(user, learnSkill2, SkillLevel.BEGINNER, SkillLevel.INTERMEDIATE);
        }

        logger.info("Demo user skills & learning goals assigned successfully to all users!");
    }

    private void addTeachingSkill(User user, Skill skill, SkillLevel level, int yearsExp) {
        if (!userSkillRepository.existsByUserIdAndSkillId(user.getId(), skill.getId())) {
            UserSkill userSkill = UserSkill.builder()
                    .user(user)
                    .skill(skill)
                    .skillLevel(level)
                    .experienceYears(yearsExp)
                    .teachingMode(TeachingMode.ONLINE)
                    .sessionDurationMin(60)
                    .creditsPerSession(10)
                    .learnersTaughtCount(2)
                    .avgSkillRating(BigDecimal.valueOf(4.5))
                    .description("Experienced in " + skill.getName() + " and passionate about teaching.")
                    .isDeleted(false)
                    .build();

            userSkillRepository.save(userSkill);
            logger.info("Assigned Teaching Skill '{}' to User '{} {}' (ID: {})",
                    skill.getName(), user.getFirstName(), user.getLastName(), user.getId());
        }
    }

    private void addLearningGoal(User user, Skill skill, SkillLevel currentLevel, SkillLevel targetLevel) {
        if (!learningGoalRepository.existsByUserIdAndSkillId(user.getId(), skill.getId())) {
            LearningGoal goal = LearningGoal.builder()
                    .user(user)
                    .skill(skill)
                    .currentLevel(currentLevel)
                    .targetLevel(targetLevel)
                    .isDeleted(false)
                    .build();

            learningGoalRepository.save(goal);
            logger.info("Assigned Learning Goal '{}' to User '{} {}' (ID: {})",
                    skill.getName(), user.getFirstName(), user.getLastName(), user.getId());
        }
    }
}
