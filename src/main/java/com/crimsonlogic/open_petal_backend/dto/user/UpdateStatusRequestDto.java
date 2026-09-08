package com.crimsonlogic.open_petal_backend.dto.user;

import com.crimsonlogic.open_petal_backend.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object representing a update status request dto.
 */
@Data
public class UpdateStatusRequestDto {
    @NotNull(message = "Status is mandatory")
    private AccountStatus status;
}
