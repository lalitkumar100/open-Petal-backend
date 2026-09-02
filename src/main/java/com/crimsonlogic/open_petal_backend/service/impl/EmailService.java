package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.skill.CreateUserSkillDto;
import com.crimsonlogic.open_petal_backend.dto.skill.UpdateUserSkillDto;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = "http://localhost:8080/api/v1/auth/verify?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Verify Your Email - Open Petal");
        message.setText("Please click the link below to verify your email address:\n\n" + verificationUrl);
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email (SMTP might not be configured). Verification Token is: " + token);
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "http://localhost:8080/api/v1/auth/reset-password?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Reset Your Password - Open Petal");
        message.setText("Please click the link below to reset your password:\n\n" + resetUrl);
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email. Reset Token is: " + token);
            e.printStackTrace();
        }
    }

    public static interface UserSkillService {

        UserSkill addSkillToUser(Long userId, CreateUserSkillDto dto);

        UserSkill updateUserSkill(Long userId, Long skillId, UpdateUserSkillDto dto);

        void removeSkillFromUser(Long userId, Long skillId);

        List<UserSkill> getAllSkillsByUser(Long userId);

        UserSkill getUserSkillDetails(Long userId, Long skillId);
    }
}
