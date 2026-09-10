package com.crimsonlogic.open_petal_backend.controller.admin;

import com.crimsonlogic.open_petal_backend.dto.location.LocationCreateRequest;
import com.crimsonlogic.open_petal_backend.dto.location.LocationResponse;
import com.crimsonlogic.open_petal_backend.dto.location.LocationSummaryResponse;
import com.crimsonlogic.open_petal_backend.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminLocationController Integration Tests")
class AdminLocationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private AdminLocationController adminLocationController;

    @BeforeAll
    static void beforeAll() {
        System.out.println("[SETUP] Initializing AdminLocationControllerTest suite execution...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("[TEARDOWN] Completed AdminLocationControllerTest suite execution.");
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminLocationController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    /**
     * Purpose: Verifies creation of a new physical session location by admin.
     * Preconditions: Valid LocationCreateRequest provided.
     * Expected Behavior: Returns HTTP 201 Created with LocationResponse.
     */
    @Test
    @DisplayName("Should return 201 Created on valid location creation")
    void givenValidLocationRequest_whenCreateLocation_thenReturns201AndLocation() throws Exception {
        LocationCreateRequest request = new LocationCreateRequest();
        request.setAddressLine1("100 Innovation Way");
        request.setArea("Downtown");
        request.setCity("Seattle");
        request.setPincode("98101");

        LocationResponse response = LocationResponse.builder()
                .id(1L)
                .addressLine1("100 Innovation Way")
                .city("Seattle")
                .pincode("98101")
                .build();

        given(locationService.createLocation(any(LocationCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/admin/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value("Seattle"));
    }

    /**
     * Purpose: Verifies fetching location details by location ID.
     * Preconditions: Valid Location ID exists.
     * Expected Behavior: Returns HTTP 200 OK with LocationResponse.
     */
    @Test
    @DisplayName("Should return 200 OK with location details for valid ID")
    void givenValidLocationId_whenGetLocationById_thenReturns200AndLocation() throws Exception {
        LocationResponse response = LocationResponse.builder()
                .id(1L)
                .city("Seattle")
                .pincode("98101")
                .build();

        given(locationService.getLocationById(1L)).willReturn(response);

        mockMvc.perform(get("/api/admin/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.city").value("Seattle"));
    }

    /**
     * Purpose: Verifies retrieving all active session locations as summary list.
     * Preconditions: Active locations exist in repository.
     * Expected Behavior: Returns HTTP 200 OK with list of location summaries.
     */
    @Test
    @DisplayName("Should return 200 OK with all locations summary list")
    void whenGetAllLocations_thenReturns200AndLocationList() throws Exception {
        LocationSummaryResponse summary = LocationSummaryResponse.builder()
                .id(1L)
                .city("Seattle")
                .pincode("98101")
                .build();

        given(locationService.getAllLocations()).willReturn(List.of(summary));

        mockMvc.perform(get("/api/admin/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Seattle"));
    }
}
