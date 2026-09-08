package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.location.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.dto.location.LocationResponse;
import com.crimsonlogic.open_petal_backend.dto.location.LocationSummaryResponse;
import com.crimsonlogic.open_petal_backend.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for admin location management.
 * Provides endpoints for creating and retrieving location records.
 */
@RestController
@RequestMapping("/api/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {

    private final LocationService locationService;

    /**
     * Creates a new location.
     *
     * @param request the request payload containing location details
     * @return ResponseEntity containing the created LocationResponse
     */
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody LocationCreateRequest request) {
        LocationResponse response = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a list of all locations.
     *
     * @return ResponseEntity containing a list of LocationSummaryResponse
     */
    @GetMapping
    public ResponseEntity<List<LocationSummaryResponse>> getAllLocations() {
        List<LocationSummaryResponse> response = locationService.getAllLocations();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves details of a specific location by its ID.
     *
     * @param id the unique identifier of the location
     * @return ResponseEntity containing the LocationResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long id) {
        LocationResponse response = locationService.getLocationById(id);
        return ResponseEntity.ok(response);
    }
}
