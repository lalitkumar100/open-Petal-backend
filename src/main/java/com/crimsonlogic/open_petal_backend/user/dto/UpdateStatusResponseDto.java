package com.crimsonlogic.open_petal_backend.user.dto;

import com.crimsonlogic.open_petal_backend.auth.enums.AccountStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStatusResponseDto {
    private Long authId;
    private AccountStatus status;
    private LocalDateTime updatedAt;
}
