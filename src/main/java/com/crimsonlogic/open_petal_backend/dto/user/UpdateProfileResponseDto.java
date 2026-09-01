package com.crimsonlogic.open_petal_backend.dto.user;

import com.crimsonlogic.open_petal_backend.enumerator.Gender;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UpdateProfileResponseDto {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private Gender gender;
    private LocalDateTime updatedAt;
}
