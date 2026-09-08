package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.location.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.dto.location.LocationResponse;
import com.crimsonlogic.open_petal_backend.dto.location.LocationSummaryResponse;
import java.util.List;

/**
 * Service interface for location operations.
 */
public interface LocationService {
    /**
     * Create Location.
     */
    LocationResponse createLocation(LocationCreateRequest request);
    /**
     * Get Location By Id.
     */
    LocationResponse getLocationById(Long id);
    /**
     * Get All Locations.
     */
    List<LocationSummaryResponse> getAllLocations();
}
