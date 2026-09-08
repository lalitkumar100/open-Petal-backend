package com.crimsonlogic.open_petal_backend.dto.aichat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a user context.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private String id;
    private String role;
    private String name;
}
