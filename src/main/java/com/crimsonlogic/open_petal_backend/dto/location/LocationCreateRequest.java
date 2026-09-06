package com.crimsonlogic.open_petal_backend.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationCreateRequest {
    @NotBlank(message = "Address Line 1 is mandatory")
    @Size(max = 255, message = "Address Line 1 must not exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address Line 2 must not exceed 255 characters")
    private String addressLine2;

    @NotBlank(message = "Area is mandatory")
    @Size(max = 100, message = "Area must not exceed 100 characters")
    private String area;

    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "Pincode is mandatory")
    @Size(max = 20, message = "Pincode must not exceed 20 characters")
    private String pincode;
}
