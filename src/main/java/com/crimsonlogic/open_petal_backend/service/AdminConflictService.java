package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.ResolveConflictRequestDto;

import java.util.List;

/**
 * Service interface for admin conflict operations.
 */
public interface AdminConflictService {
    /**
     * Get All Conflicts For Admin.
     */
    List<AdminConflictSummaryResponseDto> getAllConflictsForAdmin();
    /**
     * Get Admin Conflict Details.
     */
    AdminConflictDetailResponseDto getAdminConflictDetails(Long conflictId);
    /**
     * Resolve Conflict.
     */
    void resolveConflict(Long conflictId, Long adminUserId, ResolveConflictRequestDto request);
}
