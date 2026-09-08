package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.auth.RegisterRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.RegisterResponseDto;
import com.crimsonlogic.open_petal_backend.entity.Login;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LoginRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.service.AuthService;
import com.crimsonlogic.open_petal_backend.dto.auth.LoginRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.LoginResponseDto;
import com.crimsonlogic.open_petal_backend.dto.user.ChangePasswordRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.ForgotPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.dto.auth.ResetPasswordRequestDto;
import com.crimsonlogic.open_petal_backend.enums.AccountStatus;
import com.crimsonlogic.open_petal_backend.util.JwtUtil;

import java.time.LocalDateTime;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.exception.AuthenticationException;
import com.crimsonlogic.open_petal_backend.exception.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the auth service.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final LoginRepository loginRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthServiceImpl(LoginRepository loginRepository, UserRepository userRepository, JwtUtil jwtUtil, EmailService emailService) {
        this.loginRepository = loginRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    /**
     * Registers a new user in the system.
     * Validates that the email is not already in use, creates a new Login credential,
     * hashes the password, and creates the associated User profile.
     * An email verification token is then generated and dispatched.
     *
     * @param request the registration details containing email, password, and personal info
     * @return RegisterResponseDto containing the newly created user's basic information
     * @throws CustomException if the email is already registered
     */
    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto request) {
        if (loginRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email is already registered in the system", HttpStatus.CONFLICT);
        }

        Login login = new Login();
        login.setEmail(request.getEmail());
        login.hashPassword(request.getPassword());
        login.setLastLoginAt(LocalDateTime.now());
      
        
        login = loginRepository.save(login);

        User user = new User();
        user.setLogin(login);
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        
        user = userRepository.save(user);

        String token = jwtUtil.generateVerificationToken(user.getEmail());
        emailService.sendVerificationEmail(user.getEmail(), token);

        return RegisterResponseDto.builder()
                .userId(user.getId())
                .authId(login.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(login.getRole())
                .status(login.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Authenticates a user and generates a JWT token for session management.
     * Retrieves the login credentials by email, verifies the password hash, and checks
     * the account status to ensure it's ACTIVE. Updates the last login timestamp upon success.
     *
     * @param request the login credentials (email and password)
     * @return LoginResponseDto containing the JWT token and user profile details
     * @throws AuthenticationException if the email or password is incorrect
     * @throws AuthorizationException if the account is blocked or inactive
     */
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        Login login = loginRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!login.verifyPassword(request.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        if (login.getStatus() == AccountStatus.BLOCKED) {
            throw new AuthorizationException("Account is blocked. Please contact support.");
        } else if (login.getStatus() == AccountStatus.INACTIVE) {
            throw new AuthorizationException("Account is inactive.");
        }

        login.setLastLoginAt(LocalDateTime.now());
        loginRepository.save(login);

        String token = jwtUtil.generateToken(login.getEmail(), login.getRole());
        User user = login.getUser();

        return LoginResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400) // 1 day in seconds
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(login.getRole())
                .status(login.getStatus())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            return false;
        }

        String email = jwtUtil.extractEmail(token);
        Login login = loginRepository.findByEmail(email).orElse(null);
        if (login == null || login.getStatus() != AccountStatus.ACTIVE) {
            return false;
        }

        java.util.Date issuedAt = jwtUtil.extractIssuedAt(token);
        java.util.Date updatedAt = java.util.Date.from(login.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant());

        // Token is invalid if it was issued before the last update to the Login record (with a 2-second buffer for DB commit delays)
        return issuedAt.getTime() >= (updatedAt.getTime() - 2000);
    }

    @Override
    public String getRoleFromToken(String token) {
        return jwtUtil.extractRole(token);
    }

    @Override
    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }

    /**
     * Updates the user's password after verifying their current password.
     *
     * @param email the email of the authenticated user
     * @param request the request containing the current password and the new password
     * @throws CustomException if the user is not found
     * @throws AuthenticationException if the current password does not match
     */
    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequestDto request) {
        Login login = loginRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (!login.verifyPassword(request.getCurrentPassword())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        login.hashPassword(request.getNewPassword());
        loginRepository.save(login);
    }

    /**
     * Verifies a user's email address using a secure JWT token.
     * Parses the token claims, checks for expiration and type, and updates the
     * user's account status to ACTIVE if successful.
     *
     * @param token the verification JWT token sent via email
     * @throws AuthenticationException if the token is invalid, expired, or of the wrong type
     * @throws CustomException if the user is not found or already verified
     */
    @Override
    @Transactional
    public void verifyEmail(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new AuthenticationException("Invalid or expired verification token");
        }
        
        String type = jwtUtil.extractAllClaims(token).get("type", String.class);
        if (!"EMAIL_VERIFICATION".equals(type)) {
            throw new AuthenticationException("Invalid token type");
        }
        
        String email = jwtUtil.extractEmail(token);
        Login login = loginRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
                
        if (login.getStatus() == AccountStatus.ACTIVE) {
            throw new CustomException("Email is already verified", HttpStatus.BAD_REQUEST);
        }
        
        login.setStatus(AccountStatus.ACTIVE);
        loginRepository.save(login);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDto request) {
        Login login = loginRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        String token = jwtUtil.generatePasswordResetToken(login.getEmail());
        emailService.sendPasswordResetEmail(login.getEmail(), token);
    }

    /**
     * Resets a user's password using a secure reset token.
     * Validates the token's type, expiration, and ensures it hasn't been invalidated
     * by a previous password change. Hashes and saves the new password.
     *
     * @param token the password reset JWT token
     * @param request the new password payload
     * @throws AuthenticationException if the token is invalid, expired, or invalidated
     * @throws CustomException if the user is not found
     */
    @Override
    @Transactional
    public void resetPassword(String token, ResetPasswordRequestDto request) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new AuthenticationException("Invalid or expired password reset token");
        }
        
        String type = jwtUtil.extractAllClaims(token).get("type", String.class);
        if (!"PASSWORD_RESET".equals(type)) {
            throw new AuthenticationException("Invalid token type");
        }
        
        String email = jwtUtil.extractEmail(token);
        Login login = loginRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        java.util.Date issuedAt = jwtUtil.extractIssuedAt(token);
        java.util.Date updatedAt = java.util.Date.from(login.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant());

        // Token is invalid if it was issued before the last update to the Login record
        if (issuedAt.getTime() < (updatedAt.getTime() - 2000)) {
            throw new AuthenticationException("Password reset token is no longer valid");
        }
                
        login.hashPassword(request.getNewPassword());
        loginRepository.save(login);
    }

    /**
     * Updates the active status of the authenticated user.
     * Allows users to deactivate or activate their own accounts.
     *
     * @param email the email of the authenticated user
     * @param newStatus the new AccountStatus to apply
     * @return the updated Login entity
     * @throws RecordNotFoundException if the user is not found
     */
    @Override
    @Transactional
    public Login changeSelfStatus(String email, AccountStatus newStatus) {
        Login login = loginRepository.findByEmail(email)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
                
        login.setStatus(newStatus);
        return loginRepository.save(login);
    }
}
