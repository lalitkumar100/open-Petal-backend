package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.dto.admin.ResolveConflictRequestDto;

import java.util.List;

public interface AdminConflictService {
    List<AdminConflictSummaryResponseDto> getAllConflictsForAdmin();
    AdminConflictDetailResponseDto getAdminConflictDetails(Long conflictId);
    void resolveConflict(Long conflictId, Long adminUserId, ResolveConflictRequestDto request);
}
