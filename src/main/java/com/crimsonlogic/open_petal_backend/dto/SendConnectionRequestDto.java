package com.crimsonlogic.open_petal_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendConnectionRequestDto {
    private Long receiverId;
    private Long primarySkillId;
    private Long offeredSkillId; // optional
    private String initialMessage; // optional
}
