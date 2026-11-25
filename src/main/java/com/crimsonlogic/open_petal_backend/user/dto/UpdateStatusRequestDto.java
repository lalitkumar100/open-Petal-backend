package com.crimsonlogic.open_petal_backend.user.dto;

import com.crimsonlogic.open_petal_backend.auth.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequestDto {
    @NotNull(message = "Status is mandatory")
    private AccountStatus status;
}
