package com.crimsonlogic.open_petal_backend.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "First name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name must contain only letters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name must contain only letters")
    private String lastName;

    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dob;
    
    @AssertTrue(message = "You must be at least 28 years old")
    public boolean isOfValidAge() {
        if (dob == null) {
            return false;
        }
        return java.time.Period.between(dob, LocalDate.now()).getYears() >= 28;
    }
}
