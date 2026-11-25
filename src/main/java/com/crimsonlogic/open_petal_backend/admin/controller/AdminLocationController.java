package com.crimsonlogic.open_petal_backend.admin.controller;

import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.location.dto.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.location.dto.LocationResponse;
import com.crimsonlogic.open_petal_backend.location.dto.LocationSummaryResponse;
import com.crimsonlogic.open_petal_backend.location.service.LocationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {

    private final LocationService locationService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(@Valid @RequestBody LocationCreateRequest request) {
        LocationResponse response = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                MessageCodes.LOCATION_CREATED,
                messageService.getMessage(MessageCodes.LOCATION_CREATED),
                response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationSummaryResponse>>> getAllLocations() {
        List<LocationSummaryResponse> response = locationService.getAllLocations();
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.LOCATIONS_RETRIEVED,
                messageService.getMessage(MessageCodes.LOCATIONS_RETRIEVED),
                response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationById(@PathVariable Long id) {
        LocationResponse response = locationService.getLocationById(id);
        return ResponseEntity.ok(ApiResponse.success(
                MessageCodes.LOCATION_RETRIEVED,
                messageService.getMessage(MessageCodes.LOCATION_RETRIEVED),
                response));
    }
}
