package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.location.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.dto.location.LocationResponse;
import com.crimsonlogic.open_petal_backend.dto.location.LocationSummaryResponse;
import java.util.List;

public interface LocationService {
    LocationResponse createLocation(LocationCreateRequest request);
    LocationResponse getLocationById(Long id);
    List<LocationSummaryResponse> getAllLocations();
}
