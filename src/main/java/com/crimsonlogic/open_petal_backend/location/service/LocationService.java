package com.crimsonlogic.open_petal_backend.location.service;

import com.crimsonlogic.open_petal_backend.location.dto.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.location.dto.LocationResponse;
import com.crimsonlogic.open_petal_backend.location.dto.LocationSummaryResponse;
import java.util.List;

public interface LocationService {
    LocationResponse createLocation(LocationCreateRequest request);
    LocationResponse getLocationById(Long id);
    List<LocationSummaryResponse> getAllLocations();
}
