package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.session.CreateSessionDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminSessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.Location;
import com.crimsonlogic.open_petal_backend.entity.Skill;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.entity.UserSkill;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.enums.TeachingMode;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.LocationRepository;
import com.crimsonlogic.open_petal_backend.repository.SkillRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.repository.UserSkillRepository;
import com.crimsonlogic.open_petal_backend.repository.SessionConflictRepository;
import com.crimsonlogic.open_petal_backend.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.crimsonlogic.open_petal_backend.dto.session.SessionSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionOtpRequestDto;
import com.crimsonlogic.open_petal_backend.dto.session.SessionConflictRequestDto;
import com.crimsonlogic.open_petal_backend.entity.SessionConflict;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;

/**
 * Implementation of the session service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final LearningSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final LocationRepository locationRepository;
    private final EmailService emailService;
    private final SessionConflictRepository sessionConflictRepository;

    /**
     * Creates a new learning session request between a learner and a mentor.
     * Validates that the learner and mentor are distinct, checks for duplicate active
     * requests, and ensures the mentor is available during the requested time slot.
     * Also validates credit requirements for CREDIT sessions and escrow deducts credits.
     *
     * @param learnerId the ID of the user requesting the session
     * @param dto the session details including mentor, time, skill, and type
     * @return the newly created LearningSession entity in REQUESTED state
     * @throws CustomException if validation fails (e.g. duplicate, unavailable, insufficient credits)
     * @throws RecordNotFoundException if the learner, mentor, or location is not found
     */
    @Override
    @Transactional
    public LearningSession createSession(Long learnerId, CreateSessionDto dto) {
        if (learnerId.equals(dto.getMentorId())) {
            throw new CustomException("Learner and Mentor cannot be the same user", HttpStatus.BAD_REQUEST);
        }

        User learner = userRepository.findById(learnerId)
                .orElseThrow(() -> new RecordNotFoundException("Learner not found"));
        User mentor = userRepository.findById(dto.getMentorId())
                .orElseThrow(() -> new RecordNotFoundException("Mentor not found"));

        // Prevent duplicate requests
        List<SessionStatus> activeStatuses = Arrays.asList(
                SessionStatus.REQUESTED, 
                SessionStatus.SCHEDULED, 
                SessionStatus.STARTED
        );
        
        boolean duplicateExists = sessionRepository.existsActiveSessionRequest(
                learnerId, 
                mentor.getId(), 
                dto.getPrimarySkillId(), 
                activeStatuses
        );

        if (duplicateExists) {
            throw new CustomException(
                    "You already have a pending session request for this skill with this mentor. Please wait for their response or cancel your previous request before submitting a new one.", 
                    HttpStatus.CONFLICT
            );
        }

        // Check for timeslot conflicts
        boolean overlap = sessionRepository.existsOverlappingSession(
                mentor.getId(), dto.getSessionDate(), dto.getStartTime(), dto.getEndTime());
        if (overlap) {
            throw new CustomException("The mentor is unavailable during the selected time slot.", HttpStatus.CONFLICT);
        }

        // Get the mentor's skill configuration
        UserSkill mentorSkill = userSkillRepository.findByUserIdAndSkillIdAndIsDeletedFalse(mentor.getId(), dto.getPrimarySkillId())
                .orElseThrow(() -> new CustomException("Mentor does not teach the specified skill", HttpStatus.BAD_REQUEST));

        Skill primarySkill = mentorSkill.getSkill();

        // Validate Duration
        long durationMinutes = Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();
        if (durationMinutes < mentorSkill.getSessionDurationMin()) {
            throw new CustomException("Session duration must be at least " + mentorSkill.getSessionDurationMin() + " minutes", HttpStatus.BAD_REQUEST);
        }

        // Validate Credits and Session Type
        if (dto.getSessionType() == SessionType.CREDIT) {
            if (!dto.getCreditsHeld().equals(mentorSkill.getCreditsPerSession())) {
                throw new CustomException("Credits held must exactly match the mentor's required credits per session (" + mentorSkill.getCreditsPerSession() + ")", HttpStatus.BAD_REQUEST);
            }
            if (learner.getCredit() < dto.getCreditsHeld()) {
                throw new CustomException("Insufficient credit balance to request this session.", HttpStatus.BAD_REQUEST);
            }
            // Deduct credits as escrow
            learner.setCredit(learner.getCredit() - dto.getCreditsHeld());
            userRepository.save(learner);
        } else if (dto.getSessionType() == SessionType.BARTER) {
            if (dto.getCreditsHeld() != null && dto.getCreditsHeld() != 0) {
                throw new CustomException("Credits held must be 0 for a BARTER session", HttpStatus.BAD_REQUEST);
            }
            if (dto.getOfferedSkillId() == null) {
                throw new CustomException("Offered skill is required for a BARTER session", HttpStatus.BAD_REQUEST);
            }
        }

        Skill offeredSkill = null;
        if (dto.getOfferedSkillId() != null) {
            offeredSkill = skillRepository.findById(dto.getOfferedSkillId())
                    .orElseThrow(() -> new RecordNotFoundException("Offered skill not found"));
        }

        Location location = null;
        if (dto.getMode() == TeachingMode.OFFLINE) {
            if (dto.getLocationId() == null) {
                throw new CustomException("Location is required for OFFLINE mode", HttpStatus.BAD_REQUEST);
            }
            location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new RecordNotFoundException("Location not found"));
        }

        LearningSession session = LearningSession.builder()
                .learner(learner)
                .mentor(mentor)
                .sessionType(dto.getSessionType())
                .primarySkill(primarySkill)
                .offeredSkill(offeredSkill)
                .sessionDate(dto.getSessionDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .mode(dto.getMode())
                .location(location)
                .meetingLink(dto.getMode() == TeachingMode.ONLINE ? dto.getMeetingLink() : null)
                .creditsHeld(dto.getCreditsHeld() != null ? dto.getCreditsHeld() : 0)
                .status(SessionStatus.REQUESTED)
                .learnerStartPassword(generateRandomToken())
                .mentorStartPassword(generateRandomToken())
                .learnerEndPassword(generateRandomToken())
                .mentorEndPassword(generateRandomToken())
                .build();

        return sessionRepository.save(session);
    }

    /**
     * Data Flow: Executes the business logic by processing the input parameters.
     */
    private String generateRandomToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    @Override
    public LearningSession startSession(Long sessionId, Long mentorId, String otp) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public LearningSession completeSession(Long sessionId, Long learnerId, String otp) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    @Override
    public List<SessionSummaryResponseDto> getMySessions(Long userId) {
        List<LearningSession> sessions = sessionRepository.findAllUserSessions(userId);
        return sessions.stream().map(s -> {
            boolean isLearner = s.getLearner().getId().equals(userId);
            String partnerName;
            if (isLearner) {
                partnerName = s.getMentor() != null ? s.getMentor().getFirstName() + " " + s.getMentor().getLastName() : "Unknown";
            } else {
                partnerName = s.getLearner() != null ? s.getLearner().getFirstName() + " " + s.getLearner().getLastName() : "Unknown";
            }

            return SessionSummaryResponseDto.builder()
                    .id(s.getId())
                    .partnerName(partnerName)
                    .role(isLearner ? "LEARNER" : "MENTOR")
                    .primarySkillName(s.getPrimarySkill() != null ? s.getPrimarySkill().getName() : "")
                    .sessionType(s.getSessionType())
                    .sessionDate(s.getSessionDate())
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .mode(s.getMode())
                    .status(s.getStatus())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public SessionDetailResponseDto getSessionDetails(Long sessionId, Long userId) {
        LearningSession s = sessionRepository.findSessionWithDetailsById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        
        if (!s.getLearner().getId().equals(userId) && !s.getMentor().getId().equals(userId)) {
            throw new CustomException("Forbidden to access this session", HttpStatus.FORBIDDEN);
        }

        boolean isMentor = s.getMentor().getId().equals(userId);
        boolean isLearner = s.getLearner().getId().equals(userId);
        
        String partnerName;
        if (isLearner) {
            partnerName = s.getMentor() != null ? s.getMentor().getFirstName() + " " + s.getMentor().getLastName() : "Unknown";
        } else {
            partnerName = s.getLearner() != null ? s.getLearner().getFirstName() + " " + s.getLearner().getLastName() : "Unknown";
        }

        boolean canAccept = isMentor && s.getStatus() == SessionStatus.REQUESTED;
        boolean canDecline = isMentor && s.getStatus() == SessionStatus.REQUESTED;
        boolean canCancel = (isLearner || isMentor) && (s.getStatus() == SessionStatus.REQUESTED || s.getStatus() == SessionStatus.SCHEDULED);

        return SessionDetailResponseDto.detailBuilder()
                .id(s.getId())
                .partnerName(partnerName)
                .role(isLearner ? "LEARNER" : "MENTOR")
                .primarySkillName(s.getPrimarySkill() != null ? s.getPrimarySkill().getName() : "")
                .sessionType(s.getSessionType())
                .sessionDate(s.getSessionDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .mode(s.getMode())
                .status(s.getStatus())
                .offeredSkillName(s.getOfferedSkill() != null ? s.getOfferedSkill().getName() : null)
                .creditsHeld(s.getCreditsHeld())
                .meetingLink(s.getMeetingLink())
                .locationName(s.getLocation() != null ? s.getLocation().getAddressLine1() + ", " + s.getLocation().getCity() : null)
                .createdAt(s.getCreatedAt())
                .isMentor(isMentor)
                .canAccept(canAccept)
                .canDecline(canDecline)
                .canCancel(canCancel)
                .learnerVerifiedStart(s.getLearnerVerifiedStart() != null ? s.getLearnerVerifiedStart() : false)
                .mentorVerifiedStart(s.getMentorVerifiedStart() != null ? s.getMentorVerifiedStart() : false)
                .learnerVerifiedEnd(s.getLearnerVerifiedEnd() != null ? s.getLearnerVerifiedEnd() : false)
                .mentorVerifiedEnd(s.getMentorVerifiedEnd() != null ? s.getMentorVerifiedEnd() : false)
                .build();
    }

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, and persists changes to the database.
     */
    @Override
    @Transactional
    public void cancelSession(Long sessionId, Long userId) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));

        if (!session.getLearner().getId().equals(userId) && !session.getMentor().getId().equals(userId)) {
            throw new CustomException("Forbidden to access this session", HttpStatus.FORBIDDEN);
        }

        if (session.getStatus() == SessionStatus.CANCELLED || session.getStatus() == SessionStatus.ENDED || session.getStatus() == SessionStatus.STARTED || session.getStatus() == SessionStatus.CONFLICT) {
            throw new CustomException("Session cannot be cancelled in its current state", HttpStatus.BAD_REQUEST);
        }

        if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
            User learner = session.getLearner();
            learner.setCredit(learner.getCredit() + session.getCreditsHeld());
            session.setCreditsHeld(0); // Prevents double refunds
            userRepository.save(learner);
        }

        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);
    }

    /**
     * Accepts a pending session request.
     * Only the designated mentor can perform this action. Transitions the session to SCHEDULED
     * and generates the one-time passwords (OTPs) required for session start and end verification.
     *
     * @param sessionId the ID of the session request
     * @param currentUserId the ID of the mentor accepting the request
     * @throws CustomException if the user is not the mentor or the session is not REQUESTED
     * @throws RecordNotFoundException if the session is not found
     */
    @Override
    @Transactional
    public void acceptSession(Long sessionId, Long currentUserId) {
        LearningSession session = sessionRepository.findSessionWithDetailsById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        
        if (!session.getMentor().getId().equals(currentUserId)) {
            throw new CustomException("Only the designated mentor can accept this session request.", HttpStatus.FORBIDDEN);
        }
        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new CustomException("Session is not in a requested state.", HttpStatus.BAD_REQUEST);
        }
        
        session.setStatus(SessionStatus.SCHEDULED);
        
        if (session.getLearnerStartPassword() == null) {
            session.setLearnerStartPassword(generateRandomToken());
            session.setMentorStartPassword(generateRandomToken());
            session.setLearnerEndPassword(generateRandomToken());
            session.setMentorEndPassword(generateRandomToken());
        }
        
        sessionRepository.save(session);
    }

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, and persists changes to the database.
     */
    @Override
    @Transactional
    public void declineSession(Long sessionId, Long currentUserId) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        
        if (!session.getMentor().getId().equals(currentUserId)) {
            throw new CustomException("Only the designated mentor can decline this session request.", HttpStatus.FORBIDDEN);
        }
        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new CustomException("Session is not in a requested state.", HttpStatus.BAD_REQUEST);
        }
        
        if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
            User learner = session.getLearner();
            learner.setCredit(learner.getCredit() + session.getCreditsHeld());
            session.setCreditsHeld(0); // Prevents double refunds
            userRepository.save(learner);
        }
        
        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);
    }

    @Override
    public java.util.List<AdminSessionSummaryResponseDto> getAllGlobalSessions() {
        return sessionRepository.findAllGlobalSessions().stream().map(s -> {
            return AdminSessionSummaryResponseDto.builder()
                    .id(s.getId())
                    .learnerName(s.getLearner() != null ? s.getLearner().getFirstName() + " " + s.getLearner().getLastName() : "Unknown")
                    .learnerEmail(s.getLearner() != null ? s.getLearner().getEmail() : "Unknown")
                    .mentorName(s.getMentor() != null ? s.getMentor().getFirstName() + " " + s.getMentor().getLastName() : "Unknown")
                    .mentorEmail(s.getMentor() != null ? s.getMentor().getEmail() : "Unknown")
                    .primarySkillName(s.getPrimarySkill() != null ? s.getPrimarySkill().getName() : "Unknown")
                    .sessionType(s.getSessionType())
                    .creditsHeld(s.getCreditsHeld())
                    .mode(s.getMode())
                    .status(s.getStatus())
                    .sessionDate(s.getSessionDate())
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .createdAt(s.getCreatedAt())
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public AdminSessionDetailResponseDto getAdminSessionDetails(Long sessionId) {
        LearningSession s = sessionRepository.findSessionWithDetailsById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        return AdminSessionDetailResponseDto.adminDetailBuilder()
                .id(s.getId())
                .learnerName(s.getLearner() != null ? s.getLearner().getFirstName() + " " + s.getLearner().getLastName() : "Unknown")
                .learnerEmail(s.getLearner() != null ? s.getLearner().getEmail() : "Unknown")
                .mentorName(s.getMentor() != null ? s.getMentor().getFirstName() + " " + s.getMentor().getLastName() : "Unknown")
                .mentorEmail(s.getMentor() != null ? s.getMentor().getEmail() : "Unknown")
                .primarySkillName(s.getPrimarySkill() != null ? s.getPrimarySkill().getName() : "Unknown")
                .sessionType(s.getSessionType())
                .creditsHeld(s.getCreditsHeld())
                .mode(s.getMode())
                .status(s.getStatus())
                .sessionDate(s.getSessionDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .createdAt(s.getCreatedAt())
                .locationName(s.getLocation() != null ? s.getLocation().getAddressLine1() + ", " + s.getLocation().getCity() : null)
                .meetingLink(s.getMeetingLink())
                .learnerStartPassword(s.getLearnerStartPassword())
                .mentorStartPassword(s.getMentorStartPassword())
                .learnerEndPassword(s.getLearnerEndPassword())
                .mentorEndPassword(s.getMentorEndPassword())
                .offeredSkillName(s.getOfferedSkill() != null ? s.getOfferedSkill().getName() : null)
                .build();
    }

    @Override
    public String getMyOtp(Long sessionId, Long userId) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        
        if (session.getStatus() != SessionStatus.SCHEDULED && session.getStatus() != SessionStatus.STARTED) {
            throw new CustomException("Session is not in a valid state for OTP retrieval", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        if (session.getStatus() == SessionStatus.SCHEDULED) {
            LocalDateTime sessionStart = session.getSessionDate().atTime(session.getStartTime());
            if (now.isBefore(sessionStart.minusMinutes(15)) || now.isAfter(sessionStart.plusMinutes(15))) {
                throw new CustomException("Outside the 15-minute start window", HttpStatus.BAD_REQUEST);
            }
            if (session.getLearner().getId().equals(userId)) {
                return session.getLearnerStartPassword();
            } else if (session.getMentor().getId().equals(userId)) {
                return session.getMentorStartPassword();
            }
        } else if (session.getStatus() == SessionStatus.STARTED) {
            LocalDateTime sessionEnd = session.getSessionDate().atTime(session.getEndTime());
            if (now.isBefore(sessionEnd.minusMinutes(15)) || now.isAfter(sessionEnd.plusMinutes(15))) {
                throw new CustomException("Outside the 15-minute end window", HttpStatus.BAD_REQUEST);
            }
            if (session.getLearner().getId().equals(userId)) {
                return session.getLearnerEndPassword();
            } else if (session.getMentor().getId().equals(userId)) {
                return session.getMentorEndPassword();
            }
        }
        
        throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
    }

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, persists changes to the database, and maps the result to a Data Transfer Object (DTO).
     */
    @Override
    @Transactional
    public void verifyStart(Long sessionId, Long userId, SessionOtpRequestDto dto) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        
        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new CustomException("Session is not in a scheduled state", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime sessionStart = session.getSessionDate().atTime(session.getStartTime());
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(sessionStart.minusMinutes(15)) || now.isAfter(sessionStart.plusMinutes(15))) {
            throw new CustomException("Outside the 15-minute start window", HttpStatus.BAD_REQUEST);
        }

        if (session.getLearner().getId().equals(userId)) {
            if (!session.getMentorStartPassword().equals(dto.getOtp())) {
                throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
            }
            session.setLearnerVerifiedStart(true);
        } else if (session.getMentor().getId().equals(userId)) {
            if (!session.getLearnerStartPassword().equals(dto.getOtp())) {
                throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
            }
            session.setMentorVerifiedStart(true);
        } else {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }
        
        if (session.getLearnerVerifiedStart() && session.getMentorVerifiedStart()) {
            session.setStatus(SessionStatus.STARTED);
        }
        sessionRepository.save(session);
    }



    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, persists changes to the database, and maps the result to a Data Transfer Object (DTO).
     */
    @Override
    @Transactional
    public void verifyEnd(Long sessionId, Long userId, SessionOtpRequestDto dto) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        
        if (session.getStatus() != SessionStatus.STARTED) {
            throw new CustomException("Session is not in a started state", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime sessionEnd = session.getSessionDate().atTime(session.getEndTime());
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(sessionEnd.minusMinutes(15)) || now.isAfter(sessionEnd.plusMinutes(15))) {
            throw new CustomException("Outside the 15-minute end window", HttpStatus.BAD_REQUEST);
        }

        if (session.getLearner().getId().equals(userId)) {
            if (!session.getMentorEndPassword().equals(dto.getOtp())) {
                throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
            }
            session.setLearnerVerifiedEnd(true);
        } else if (session.getMentor().getId().equals(userId)) {
            if (!session.getLearnerEndPassword().equals(dto.getOtp())) {
                throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
            }
            session.setMentorVerifiedEnd(true);
        } else {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }
        
        if (session.getLearnerVerifiedEnd() && session.getMentorVerifiedEnd()) {
            session.setStatus(SessionStatus.ENDED);
            if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
                User mentor = session.getMentor();
                mentor.setCredit(mentor.getCredit() + session.getCreditsHeld());
                session.setCreditsHeld(0);
                userRepository.save(mentor);
            }
        }
        sessionRepository.save(session);
    }

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, persists changes to the database, and maps the result to a Data Transfer Object (DTO).
     */
    @Override
    @Transactional
    public void raiseConflict(Long sessionId, Long userId, SessionConflictRequestDto dto) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));

        if (session.getStatus() == SessionStatus.CANCELLED || session.getStatus() == SessionStatus.ENDED) {
            throw new CustomException("Cannot raise conflict on a completed or cancelled session", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sessionStart = session.getSessionDate().atTime(session.getStartTime());
        LocalDateTime sessionEnd = session.getSessionDate().atTime(session.getEndTime());
        if (now.isBefore(sessionStart) || now.isAfter(sessionEnd.plusMinutes(15))) {
            throw new CustomException("Conflicts can only be raised between session start and 15 minutes after session end.", HttpStatus.BAD_REQUEST);
        }

        User raiser = null;
        String learnerStory = null;
        String mentorStory = null;

        if (session.getLearner().getId().equals(userId)) {
            raiser = session.getLearner();
            learnerStory = dto.getStory();
        } else if (session.getMentor().getId().equals(userId)) {
            raiser = session.getMentor();
            mentorStory = dto.getStory();
        } else {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }

        SessionConflict conflict = SessionConflict.builder()
                .session(session)
                .raisedBy(raiser)
                .reason(dto.getReason())
                .learnerStory(learnerStory)
                .mentorStory(mentorStory)
                .status(ConflictStatus.OPEN)
                .build();
        
        sessionConflictRepository.save(conflict);

        session.setStatus(SessionStatus.CONFLICT);
        sessionRepository.save(session);
        
        log.info("Conflict raised for session ID: {} by user ID: {}", sessionId, userId);
    }

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, and persists changes to the database.
     */
    @Override
    @Transactional
    public void adminCancelSession(Long sessionId, String reason) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));
        
        if (session.getStatus() == SessionStatus.CANCELLED) {
            throw new CustomException("Session cannot be cancelled in its current state", HttpStatus.BAD_REQUEST);
        }
        
        if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
            User learner = session.getLearner();
            learner.setCredit(learner.getCredit() + session.getCreditsHeld());
            session.setCreditsHeld(0); // Prevents double refunds
            userRepository.save(learner);
        }
        
        session.setStatus(SessionStatus.CANCELLED);
        System.out.println("Admin cancelled session " + sessionId + ". Reason: " + reason);
        sessionRepository.save(session);
    }

    @Override
    public com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto getSessionConflict(Long sessionId, Long currentUserId) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));

        if (!session.getLearner().getId().equals(currentUserId) && !session.getMentor().getId().equals(currentUserId)) {
            throw new CustomException("Forbidden to access this session's conflict", HttpStatus.FORBIDDEN);
        }

        SessionConflict conflict = sessionConflictRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Conflict details not found for this session"));

        return com.crimsonlogic.open_petal_backend.dto.session.SessionConflictDetailResponseDto.builder()
                .conflictId(conflict.getId())
                .sessionId(session.getId())
                .status(conflict.getStatus())
                .raisedByName(conflict.getRaisedBy().getFirstName() + " " + conflict.getRaisedBy().getLastName())
                .raisedByMe(conflict.getRaisedBy().getId().equals(currentUserId))
                .learnerStory(conflict.getLearnerStory())
                .mentorStory(conflict.getMentorStory())
                .adminResolutionNotes(conflict.getAdminResolutionNotes())
                .createdAt(conflict.getCreatedAt())
                .build();
    }

    /**
     * Data Flow: This method performs input/state validation, retrieves the entity from the database, and persists changes to the database.
     */
    @Override
    @Transactional
    public void submitConflictStory(Long sessionId, Long currentUserId, String story) {
        LearningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Session not found"));

        if (!session.getLearner().getId().equals(currentUserId) && !session.getMentor().getId().equals(currentUserId)) {
            throw new CustomException("Forbidden to access this session's conflict", HttpStatus.FORBIDDEN);
        }

        SessionConflict conflict = sessionConflictRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RecordNotFoundException("Conflict details not found for this session"));

        if (conflict.getStatus() != ConflictStatus.OPEN) {
            throw new CustomException("Conflict is already resolved or closed. No further statements can be submitted.", HttpStatus.BAD_REQUEST);
        }

        if (session.getLearner().getId().equals(currentUserId)) {
            if (conflict.getLearnerStory() != null && !conflict.getLearnerStory().isBlank()) {
                throw new CustomException("Learner story has already been submitted.", HttpStatus.BAD_REQUEST);
            }
            conflict.setLearnerStory(story.trim());
        } else if (session.getMentor().getId().equals(currentUserId)) {
            if (conflict.getMentorStory() != null && !conflict.getMentorStory().isBlank()) {
                throw new CustomException("Mentor story has already been submitted.", HttpStatus.BAD_REQUEST);
            }
            conflict.setMentorStory(story.trim());
        }

        sessionConflictRepository.save(conflict);
        log.info("Counter-story submitted for session ID: {} by user ID: {}", sessionId, currentUserId);
    }
}
