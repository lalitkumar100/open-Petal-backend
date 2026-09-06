package com.crimsonlogic.open_petal_backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
