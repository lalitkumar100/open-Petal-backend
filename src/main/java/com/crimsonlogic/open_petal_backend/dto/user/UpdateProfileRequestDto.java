package com.crimsonlogic.open_petal_backend.dto.user;

import com.crimsonlogic.open_petal_backend.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object representing a update profile request dto.
 */
@Data
public class UpdateProfileRequestDto {
    @NotBlank(message = "First name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name must contain only letters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name must contain only letters")
    private String lastName;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dob;

    @NotNull(message = "Gender is mandatory")
    private Gender gender;

    @Size(max = 300, message = "Description cannot exceed 300 characters")
    private String description;

    @jakarta.validation.constraints.AssertTrue(message = "You must be at least 18 years old")
    public boolean isAdult() {
        if (dob == null) return true;
        return java.time.Period.between(dob, java.time.LocalDate.now()).getYears() >= 18;
    }
}
