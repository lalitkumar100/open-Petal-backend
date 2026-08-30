package com.crimsonlogic.open_petal_backend.dto;

import com.crimsonlogic.open_petal_backend.enumerator.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequestDto {
    @NotBlank(message = "First name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name must contain only letters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name must contain only letters")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number is invalid")
    private String phone;

    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dob;

    private Gender gender;
}
