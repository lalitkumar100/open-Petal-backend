package com.crimsonlogic.open_petal_backend.dto.user;

import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UpdateStatusResponseDto {
    private Long authId;
    private AccountStatus status;
    private LocalDateTime updatedAt;
}
