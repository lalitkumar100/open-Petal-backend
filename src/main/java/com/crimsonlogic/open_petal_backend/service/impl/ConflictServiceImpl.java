package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.conflict.UserConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.entity.SessionConflict;
import com.crimsonlogic.open_petal_backend.repository.SessionConflictRepository;
import com.crimsonlogic.open_petal_backend.service.ConflictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConflictServiceImpl implements ConflictService {

    private final SessionConflictRepository sessionConflictRepository;

    @Override
    public List<UserConflictSummaryResponseDto> getMyConflicts(Long userId) {
        List<SessionConflict> conflicts = sessionConflictRepository.findAllMyConflicts(userId);
        
        return conflicts.stream().map(c -> {
            boolean isLearner = c.getSession().getLearner().getId().equals(userId);
            String partnerName = isLearner ? c.getSession().getMentor().getFullName()
                                           : c.getSession().getLearner().getFullName();
            
            return UserConflictSummaryResponseDto.builder()
                .conflictId(c.getId())
                .sessionId(c.getSession().getId())
                .partnerName(partnerName)
                .myRole(isLearner ? "LEARNER" : "MENTOR")
                .primarySkillName(c.getSession().getPrimarySkill().getName())
                .reason(c.getReason())
                .status(c.getStatus())
                .raisedByMe(c.getRaisedBy().getId().equals(userId))
                .createdAt(c.getCreatedAt())
                .build();
        }).collect(Collectors.toList());
    }
}
