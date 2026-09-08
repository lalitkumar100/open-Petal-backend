package com.crimsonlogic.open_petal_backend.dto.session;

import com.crimsonlogic.open_petal_backend.enums.ConflictReason;
import lombok.Data;

/**
 * Data Transfer Object representing a session conflict request dto.
 */
@Data
public class SessionConflictRequestDto {
    private ConflictReason reason;
    private String story;
}
