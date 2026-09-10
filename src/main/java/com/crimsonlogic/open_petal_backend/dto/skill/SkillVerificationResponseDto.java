package com.crimsonlogic.open_petal_backend.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillVerificationResponseDto {
    private String status; // "PASS" or "FAIL"
    private Integer marks; // out of 100
}
