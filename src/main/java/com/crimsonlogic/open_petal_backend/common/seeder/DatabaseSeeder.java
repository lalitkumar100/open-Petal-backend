package com.crimsonlogic.open_petal_backend.common.seeder;

import com.crimsonlogic.open_petal_backend.admin.dto.AddSkillRequestDto;
import com.crimsonlogic.open_petal_backend.auth.dto.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.auth.service.AuthService;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillCategoryRequestDto;
import com.crimsonlogic.open_petal_backend.skill.entity.SkillCategory;
import com.crimsonlogic.open_petal_backend.skill.service.SkillCategoryService;
import com.crimsonlogic.open_petal_backend.skill.service.SkillService;
import com.crimsonlogic.open_petal_backend.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SkillCategoryService skillCategoryService;
    private final SkillService skillService;

    public DatabaseSeeder(AuthService authService, UserRepository userRepository,
            SkillCategoryService skillCategoryService, SkillService skillService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.skillCategoryService = skillCategoryService;
        this.skillService = skillService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing Database Seeder...");

        // TOGGLE THESE TO TRUE WHEN YOU WANT TO SEED DATA
        boolean seedUsers = false;
        boolean seedCategories = false;
        boolean seedSkills = false;

        if (seedUsers) {
            logger.info("Seeding Users...");
            List<RegisterRequestDto> demoUsers = DemoData.getDemoUsers();
            for (RegisterRequestDto userDto : demoUsers) {
                if (userRepository.findByEmail(userDto.getEmail()).isEmpty()) {
                    try {
                        authService.register(userDto);
                        logger.info("Registered demo user: {}", userDto.getEmail());
                    } catch (Exception e) {
                        logger.error("Failed to register demo user: {} - Error: {}", userDto.getEmail(),
                                e.getMessage());
                    }
                } else {
                    logger.info("Demo user already exists, skipping: {}", userDto.getEmail());
                }
            }
        }

        if (seedCategories) {
            logger.info("Seeding Categories...");
            List<SkillCategoryRequestDto> demoCategories = DemoData.getDemoSkillCategories();
            for (SkillCategoryRequestDto categoryDto : demoCategories) {
                try {
                    skillCategoryService.createCategory(categoryDto);
                    logger.info("Created skill category: {}", categoryDto.getName());
                } catch (Exception e) {
                    logger.error("Failed to create category: {} - Error: {}", categoryDto.getName(),
                            e.getMessage());
                }
            }
        }

        if (seedSkills) {
            logger.info("Seeding Skills...");
            List<SkillCategory> allCategories = skillCategoryService.getAllCategories();
            Map<String, List<String>> skillsByCategory = DemoData.getDemoSkillsByCategory();
            
            for (SkillCategory category : allCategories) {
                List<String> skillNames = skillsByCategory.get(category.getName());
                if (skillNames != null) {
                    for (String skillName : skillNames) {
                        try {
                            com.crimsonlogic.open_petal_backend.admin.dto.AddSkillRequestDto requestDto = 
                                new com.crimsonlogic.open_petal_backend.admin.dto.AddSkillRequestDto(
                                    category.getId(),
                                    skillName,
                                    skillName + " skill for " + category.getName()
                                );
                            skillService.addSkillToCategory(requestDto);
                            logger.info("Created skill: {} in category: {}", skillName, category.getName());
                        } catch (Exception e) {
                            logger.error("Failed to create skill: {} - Error: {}", skillName, e.getMessage());
                        }
                    }
                }
            }
        }

        logger.info("Database seeding routines completed.");
    }
}
