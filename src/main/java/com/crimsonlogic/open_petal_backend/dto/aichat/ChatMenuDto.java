package com.crimsonlogic.open_petal_backend.dto.aichat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a chat menu dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMenuDto {
    private String id;
    private String title;
    private String date;
}
