package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.ResolveConflictRequestDto;
import com.crimsonlogic.open_petal_backend.entity.LearningSession;
import com.crimsonlogic.open_petal_backend.entity.SessionConflict;
import com.crimsonlogic.open_petal_backend.entity.User;
import com.crimsonlogic.open_petal_backend.enums.ConflictStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionStatus;
import com.crimsonlogic.open_petal_backend.enums.SessionType;
import com.crimsonlogic.open_petal_backend.exception.CustomException;
import com.crimsonlogic.open_petal_backend.exception.RecordNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LearningSessionRepository;
import com.crimsonlogic.open_petal_backend.repository.SessionConflictRepository;
import com.crimsonlogic.open_petal_backend.repository.UserRepository;
import com.crimsonlogic.open_petal_backend.service.AdminConflictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminConflictServiceImpl implements AdminConflictService {

    private final SessionConflictRepository conflictRepository;
    private final UserRepository userRepository;
    private final LearningSessionRepository sessionRepository;

    @Override
    public List<AdminConflictSummaryResponseDto> getAllConflictsForAdmin() {
        return conflictRepository.findAllGlobalConflicts().stream().map(c -> AdminConflictSummaryResponseDto.builder()
                .conflictId(c.getId())
                .sessionId(c.getSession().getId())
                .learnerName(c.getSession().getLearner().getFullName())
                .mentorName(c.getSession().getMentor().getFullName())
                .primarySkillName(c.getSession().getPrimarySkill().getName())
                .reason(c.getReason())
                .status(c.getStatus())
                .creditsHeld(c.getSession().getCreditsHeld())
                .raisedByName(c.getRaisedBy().getFullName())
                .createdAt(c.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    @Override
    public AdminConflictDetailResponseDto getAdminConflictDetails(Long conflictId) {
        SessionConflict c = conflictRepository.findAdminConflictDetailById(conflictId)
                .orElseThrow(() -> new RecordNotFoundException("Conflict not found"));

        return AdminConflictDetailResponseDto.builder()
                .conflictId(c.getId())
                .sessionId(c.getSession().getId())
                .learnerName(c.getSession().getLearner().getFullName())
                .mentorName(c.getSession().getMentor().getFullName())
                .primarySkillName(c.getSession().getPrimarySkill().getName())
                .sessionMode(c.getSession().getMode() != null ? c.getSession().getMode().name() : null)
                .sessionType(c.getSession().getSessionType().name())
                .sessionDate(c.getSession().getSessionDate())
                .startTime(c.getSession().getStartTime())
                .endTime(c.getSession().getEndTime())
                .creditsHeld(c.getSession().getCreditsHeld())
                .learnerStory(c.getLearnerStory())
                .mentorStory(c.getMentorStory())
                .reason(c.getReason())
                .status(c.getStatus())
                .raisedByName(c.getRaisedBy().getFullName())
                .adminResolutionNotes(c.getAdminResolutionNotes())
                .resolvedByName(c.getResolvedBy() != null ? c.getResolvedBy().getFullName() : null)
                .resolvedAt(c.getResolvedAt())
                .createdAt(c.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void resolveConflict(Long conflictId, Long adminUserId, ResolveConflictRequestDto request) {
        SessionConflict conflict = conflictRepository.findById(conflictId)
                .orElseThrow(() -> new RecordNotFoundException("Conflict not found"));

        if (conflict.getStatus() != ConflictStatus.OPEN && conflict.getStatus() != ConflictStatus.UNDER_REVIEW) {
            throw new CustomException("Conflict has already been resolved or dismissed.", HttpStatus.BAD_REQUEST);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RecordNotFoundException("Admin not found"));

        LearningSession session = conflict.getSession();
        String action = request.getResolutionAction();

        if ("DISMISS".equals(action) && session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
            // Default to refunding learner for CREDIT sessions to avoid stranding credits
            action = "REFUND_LEARNER";
        }

        if ("REFUND_LEARNER".equals(action)) {
            if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
                User learner = session.getLearner();
                learner.setCredit(learner.getCredit() + session.getCreditsHeld());
                userRepository.save(learner);
                session.setCreditsHeld(0);
            }
            session.setStatus(SessionStatus.CANCELLED);
            conflict.setStatus(ConflictStatus.RESOLVED_REFUNDED);
        } else if ("PAYOUT_MENTOR".equals(action)) {
            if (session.getSessionType() == SessionType.CREDIT && session.getCreditsHeld() > 0) {
                User mentor = session.getMentor();
                mentor.setCredit(mentor.getCredit() + session.getCreditsHeld());
                userRepository.save(mentor);
                session.setCreditsHeld(0);
            }
            session.setStatus(SessionStatus.ENDED);
            conflict.setStatus(ConflictStatus.RESOLVED_PAID_MENTOR);
        } else if ("DISMISS".equals(action)) {
            // Usually Barter or already zero-credit sessions
            session.setStatus(SessionStatus.CANCELLED);
            conflict.setStatus(ConflictStatus.DISMISSED);
        } else {
            throw new CustomException("Invalid resolution action", HttpStatus.BAD_REQUEST);
        }

        conflict.setAdminResolutionNotes(request.getAdminNotes());
        conflict.setResolvedBy(admin);
        conflict.setResolvedAt(LocalDateTime.now());

        sessionRepository.save(session);
        conflictRepository.save(conflict);
    }
}
