package com.crimsonlogic.open_petal_backend.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDto {
    @NotBlank(message = "Message content cannot be blank")
    private String content;
}
