package com.crimsonlogic.open_petal_backend.dto.connection;

import com.crimsonlogic.open_petal_backend.enums.ConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequestResponseDto {
    private Long id;
    private Long receiverId;
    private String receiverName;
    private String receiverInitials;
    private String primarySkillName;
    private String offeredSkillName;
    private ConnectionStatus status;
    private LocalDateTime createdAt;
}
