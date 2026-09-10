package com.crimsonlogic.open_petal_backend.service.impl;

import com.crimsonlogic.open_petal_backend.dto.location.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.dto.location.LocationResponse;
import com.crimsonlogic.open_petal_backend.dto.location.LocationSummaryResponse;
import com.crimsonlogic.open_petal_backend.entity.Location;
import com.crimsonlogic.open_petal_backend.exception.ResourceNotFoundException;
import com.crimsonlogic.open_petal_backend.repository.LocationRepository;
import com.crimsonlogic.open_petal_backend.service.LocationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationServiceImpl Mockito Unit Tests")
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    /**
     * Purpose: Validates location creation and response DTO mapping.
     * Preconditions: Valid LocationCreateRequest parameters.
     * Expected Behavior: Saves entity in repository and returns formatted LocationResponse.
     */
    @Test
    @DisplayName("Should successfully create a location")
    void givenLocationCreateRequest_whenCreateLocation_thenSavesAndReturnsResponse() {
        // Arrange
        LocationCreateRequest request = new LocationCreateRequest();
        request.setAddressLine1("123 Tech Park");
        request.setAddressLine2("Suite 400");
        request.setArea("Silicon Valley");
        request.setCity("San Jose");
        request.setPincode("95110");

        Location savedLocation = Location.builder()
                .id(1L)
                .addressLine1("123 Tech Park")
                .addressLine2("Suite 400")
                .area("Silicon Valley")
                .city("San Jose")
                .pincode("95110")
                .build();

        given(locationRepository.save(any(Location.class))).willReturn(savedLocation);

        // Act
        LocationResponse response = locationService.createLocation(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCity()).isEqualTo("San Jose");
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    /**
     * Purpose: Verifies retrieving existing non-deleted location by ID.
     * Preconditions: Location exists with isDeleted = false.
     * Expected Behavior: Returns mapped LocationResponse.
     */
    @Test
    @DisplayName("Should retrieve location by ID when exists")
    void givenExistingId_whenGetLocationById_thenReturnsLocationResponse() {
        // Arrange
        Location location = Location.builder()
                .id(2L)
                .addressLine1("456 Innovation Way")
                .city("Austin")
                .pincode("78701")
                .build();

        given(locationRepository.findByIdAndIsDeletedFalse(2L)).willReturn(Optional.of(location));

        // Act
        LocationResponse response = locationService.getLocationById(2L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getCity()).isEqualTo("Austin");
    }

    /**
     * Purpose: Ensures exception handling when location ID is not found or soft-deleted.
     * Preconditions: ID does not exist in location repository.
     * Expected Behavior: Throws ResourceNotFoundException.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when location is not found")
    void givenNonExistingId_whenGetLocationById_thenThrowsResourceNotFoundException() {
        // Arrange
        given(locationRepository.findByIdAndIsDeletedFalse(999L)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> locationService.getLocationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found with id: 999");
    }

    /**
     * Purpose: Verifies listing all non-deleted locations formatted as summaries.
     * Preconditions: Active locations exist in repository.
     * Expected Behavior: Returns list of LocationSummaryResponse objects.
     */
    @Test
    @DisplayName("Should retrieve all non-deleted active locations as summary list")
    void whenGetAllLocations_thenReturnsSummaryList() {
        // Arrange
        Location loc1 = Location.builder().id(1L).city("Bangalore").pincode("560001").build();
        Location loc2 = Location.builder().id(2L).city("Mumbai").pincode("400001").build();

        given(locationRepository.findByIsDeletedFalseOrderByCreatedAtDesc()).willReturn(List.of(loc1, loc2));

        // Act
        List<LocationSummaryResponse> list = locationService.getAllLocations();

        // Assert
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getCity()).isEqualTo("Bangalore");
        assertThat(list.get(1).getCity()).isEqualTo("Mumbai");
    }
}
