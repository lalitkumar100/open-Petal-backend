package com.crimsonlogic.open_petal_backend.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Should successfully send verification email")
    void givenEmailAndToken_whenSendVerificationEmail_thenCallsMailSender() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendVerificationEmail("test@example.com", "token-123");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
