package com.crimsonlogic.open_petal_backend.admin.service;

import com.crimsonlogic.open_petal_backend.admin.dto.AdminConflictDetailResponseDto;
import com.crimsonlogic.open_petal_backend.admin.dto.AdminConflictSummaryResponseDto;
import com.crimsonlogic.open_petal_backend.admin.dto.ResolveConflictRequestDto;
import java.util.List;

public interface AdminConflictService {
    List<AdminConflictSummaryResponseDto> getAllConflictsForAdmin();
    AdminConflictDetailResponseDto getAdminConflictDetails(Long conflictId);
    void resolveConflict(Long conflictId, Long adminUserId, ResolveConflictRequestDto request);
}
