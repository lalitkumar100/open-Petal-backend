package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.conflict.UserConflictSummaryResponseDto;

import java.util.List;

/**
 * Service interface for conflict operations.
 */
public interface ConflictService {
    /**
     * Get My Conflicts.
     */
    List<UserConflictSummaryResponseDto> getMyConflicts(Long userId);
}
