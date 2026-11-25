package com.crimsonlogic.open_petal_backend.chat.dto;

import com.crimsonlogic.open_petal_backend.chat.enums.MessageStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long connectionRequestId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime timestamp;
    private MessageStatus status;
}
