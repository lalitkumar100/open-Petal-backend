package com.crimsonlogic.open_petal_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {

    @NotBlank(message = "Current password is mandatory")
    private String currentPassword;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;
}
