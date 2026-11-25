package com.crimsonlogic.open_petal_backend.location.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String area;
    private String city;
    private String pincode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
