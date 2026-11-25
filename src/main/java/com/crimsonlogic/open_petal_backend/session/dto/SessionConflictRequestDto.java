package com.crimsonlogic.open_petal_backend.session.dto;

import com.crimsonlogic.open_petal_backend.session.enums.ConflictReason;
import lombok.Data;

@Data
public class SessionConflictRequestDto {
    private ConflictReason reason;
    private String story;
}
