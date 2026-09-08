package com.crimsonlogic.open_petal_backend.dto.connection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a send connection request dto.
 */
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
