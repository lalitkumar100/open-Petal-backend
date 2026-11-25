package com.crimsonlogic.open_petal_backend.user.dto;

import com.crimsonlogic.open_petal_backend.user.enums.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileResponseDto {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private Gender gender;
    private String description;
    private LocalDateTime updatedAt;
}
