package com.crimsonlogic.open_petal_backend.service;

import com.crimsonlogic.open_petal_backend.dto.location.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.dto.location.LocationResponse;
import com.crimsonlogic.open_petal_backend.dto.location.LocationSummaryResponse;
import com.crimsonlogic.open_petal_backend.entity.Location;
import com.crimsonlogic.open_petal_backend.exception.ResourceNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service interface for location operations.
 */
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationResponse createLocation(LocationCreateRequest request) {
        Location location = Location.builder()
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .area(request.getArea())
                .city(request.getCity())
                .pincode(request.getPincode())
                /**
                 * Build.
                 */
                .build();
        
        /**
         * Save.
         */
        Location savedLocation = locationRepository.save(location);
        /**
         * Map To Response.
         */
        return mapToResponse(savedLocation);
    }

    @Override
    public LocationResponse getLocationById(Long id) {
        Location location = locationRepository.findByIdAndIsDeletedFalse(id)
                /**
                 * Or Else Throw.
                 */
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        /**
         * Map To Response.
         */
        return mapToResponse(location);
    }

    @Override
    public List<LocationSummaryResponse> getAllLocations() {
        /**
         * Find By Is Deleted False Order By Created At Desc.
         */
        List<Location> locations = locationRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
        return locations.stream()
                .map(this::mapToSummaryResponse)
                /**
                 * Collect.
                 */
                .collect(Collectors.toList());
    }

    /**
     * Data Flow: Executes the business logic by processing the input parameters.
     */
    private LocationResponse mapToResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .addressLine1(location.getAddressLine1())
                .addressLine2(location.getAddressLine2())
                .area(location.getArea())
                .city(location.getCity())
                .pincode(location.getPincode())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                /**
                 * Build.
                 */
                .build();
    }

    /**
     * Data Flow: Executes the business logic by processing the input parameters.
     */
    private LocationSummaryResponse mapToSummaryResponse(Location location) {
        return LocationSummaryResponse.builder()
                .id(location.getId())
                .addressLine1(location.getAddressLine1())
                .area(location.getArea())
                .city(location.getCity())
                .pincode(location.getPincode())
                /**
                 * Build.
                 */
                .build();
    }
}
