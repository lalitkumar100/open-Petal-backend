package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.conflict.UserConflictSummaryResponseDto;

import java.util.List;

public interface ConflictService {
    List<UserConflictSummaryResponseDto> getMyConflicts(Long userId);
}
