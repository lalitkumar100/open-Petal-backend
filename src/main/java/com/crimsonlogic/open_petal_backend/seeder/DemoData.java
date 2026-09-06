package com.crimsonlogic.open_petal_backend.seeder;

import com.crimsonlogic.open_petal_backend.dto.auth.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.dto.category.SkillCategoryRequestDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DemoData {

    public static List<RegisterRequestDto> getDemoUsers() {
        List<RegisterRequestDto> users = new ArrayList<>();

        users.add(createRequest("john.doe@example.com", "Password123!", "John", "Doe", LocalDate.of(1985, 5, 15)));
        users.add(createRequest("jane.smith@example.com", "Password123!", "Jane", "Smith", LocalDate.of(1990, 8, 22)));
        users.add(createRequest("michael.brown@example.com", "Password123!", "Michael", "Brown", LocalDate.of(1978, 11, 2)));
        users.add(createRequest("emily.davis@example.com", "Password123!", "Emily", "Davis", LocalDate.of(1982, 3, 10)));
        users.add(createRequest("william.wilson@example.com", "Password123!", "William", "Wilson", LocalDate.of(1995, 7, 30)));
        users.add(createRequest("olivia.taylor@example.com", "Password123!", "Olivia", "Taylor", LocalDate.of(1988, 12, 12)));
        users.add(createRequest("james.anderson@example.com", "Password123!", "James", "Anderson", LocalDate.of(1975, 1, 25)));
        users.add(createRequest("sophia.thomas@example.com", "Password123!", "Sophia", "Thomas", LocalDate.of(1992, 9, 18)));
        users.add(createRequest("robert.jackson@example.com", "Password123!", "Robert", "Jackson", LocalDate.of(1980, 4, 5)));
        users.add(createRequest("isabella.white@example.com", "Password123!", "Isabella", "White", LocalDate.of(1986, 6, 20)));

        return users;
    }

    public static List<SkillCategoryRequestDto> getDemoSkillCategories() {
        return Arrays.asList(
                new SkillCategoryRequestDto("Software Development", "Programming, web development, software engineering"),
                new SkillCategoryRequestDto("Design & Creative", "Graphic design, UI/UX, video editing, animation"),
                new SkillCategoryRequestDto("Data Science & Analytics", "Machine learning, data analysis, statistics"),
                new SkillCategoryRequestDto("Digital Marketing", "SEO, social media marketing, content strategy"),
                new SkillCategoryRequestDto("Business & Management", "Project management, leadership, entrepreneurship"),
                new SkillCategoryRequestDto("Languages", "English, Spanish, French, Mandarin, etc."),
                new SkillCategoryRequestDto("Music & Audio", "Instruments, vocal coaching, audio production"),
                new SkillCategoryRequestDto("Health & Fitness", "Yoga, personal training, nutrition"),
                new SkillCategoryRequestDto("Academics", "Mathematics, physics, history, science"),
                new SkillCategoryRequestDto("Lifestyle & Hobbies", "Cooking, photography, gardening")
        );
    }

    private static RegisterRequestDto createRequest(String email, String password, String firstName, String lastName, LocalDate dob) {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail(email);
        request.setPassword(password);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setDob(dob);
        return request;
    }

    public static Map<String, List<String>> getDemoSkillsByCategory() {
        Map<String, List<String>> skills = new HashMap<>();
        skills.put("Software Development", Arrays.asList("Java", "Python", "JavaScript", "React", "Angular"));
        skills.put("Design & Creative", Arrays.asList("Photoshop", "Illustrator", "Figma", "Premiere Pro", "After Effects"));
        skills.put("Data Science & Analytics", Arrays.asList("Machine Learning", "Data Analysis", "SQL", "Pandas", "TensorFlow"));
        skills.put("Digital Marketing", Arrays.asList("SEO", "SEM", "Content Marketing", "Social Media", "Email Marketing"));
        skills.put("Business & Management", Arrays.asList("Project Management", "Agile", "Scrum", "Leadership", "Public Speaking"));
        skills.put("Languages", Arrays.asList("English", "Spanish", "French", "Mandarin", "German"));
        skills.put("Music & Audio", Arrays.asList("Piano", "Guitar", "Vocals", "Music Production", "Ableton"));
        skills.put("Health & Fitness", Arrays.asList("Yoga", "Personal Training", "Nutrition", "Pilates", "CrossFit"));
        skills.put("Academics", Arrays.asList("Mathematics", "Physics", "Chemistry", "Biology", "History"));
        skills.put("Lifestyle & Hobbies", Arrays.asList("Cooking", "Photography", "Gardening", "Baking", "Knitting"));
        return skills;
    }
}
