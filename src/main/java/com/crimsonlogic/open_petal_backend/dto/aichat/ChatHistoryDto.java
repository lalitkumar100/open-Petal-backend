package com.crimsonlogic.open_petal_backend.dto.aichat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Data Transfer Object representing a chat history dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryDto {
    private String id;
    private String title;
    private List<Object> messages;
}
