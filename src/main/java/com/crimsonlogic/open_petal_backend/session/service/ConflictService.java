package com.crimsonlogic.open_petal_backend.session.service;

import com.crimsonlogic.open_petal_backend.session.dto.UserConflictSummaryResponseDto;
import java.util.List;

public interface ConflictService {
    List<UserConflictSummaryResponseDto> getMyConflicts(Long userId);
}
