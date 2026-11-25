package com.crimsonlogic.open_petal_backend.chat.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummaryDto {
    private Long connectionRequestId;
    private Long partnerId;
    private String partnerName;
    private String partnerInitials;
    private String lastMessagePreview;
    private LocalDateTime lastMessageTimestamp;
    private Long unreadCount;
}
