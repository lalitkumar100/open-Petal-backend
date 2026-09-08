package com.crimsonlogic.open_petal_backend.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a location summary response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSummaryResponse {
    private Long id;
    private String addressLine1;
    private String area;
    private String city;
    private String pincode;
}
