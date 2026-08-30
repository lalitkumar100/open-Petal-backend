package com.crimsonlogic.open_petal_backend.dto;

import com.crimsonlogic.open_petal_backend.enumerator.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequestDto {
    @NotNull(message = "Status is mandatory")
    private AccountStatus status;
}
