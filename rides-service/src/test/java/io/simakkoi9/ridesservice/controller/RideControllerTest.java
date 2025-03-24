package io.simakkoi9.ridesservice.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.rest.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.service.RideService;
import io.simakkoi9.ridesservice.util.TestDataUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RideControllerTest {
    @Mock
    RideService rideService;
    @InjectMocks
    RideController rideController;

    RideCreateRequest rideCreateRequest;
    RideUpdateRequest rideUpdateRequest;
    RideResponse rideResponse;
    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateRide_ShouldReturnResponse_Valid() {
        rideCreateRequest = TestDataUtil.getRideCreateRequest();
        rideResponse = TestDataUtil.getRideResponse(null);
        when(rideService.createRide(rideCreateRequest)).thenReturn(rideResponse);

        RideResponse result = rideController.createRide(rideCreateRequest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(rideService).createRide(rideCreateRequest);
        verifyNoMoreInteractions(rideService);
    }

    @Test
    void testUpdateRide_ShouldReturnResponse_Valid() {
        rideUpdateRequest = TestDataUtil.getRideUpdateRequest();
        rideResponse = TestDataUtil.getRideResponse(null);
        when(rideService.updateRide(TestDataUtil.RIDE_ID, rideUpdateRequest)).thenReturn(rideResponse);

        RideResponse result = rideController.updateRide(TestDataUtil.RIDE_ID, rideUpdateRequest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(rideService).updateRide(TestDataUtil.RIDE_ID, rideUpdateRequest);
        verifyNoMoreInteractions(rideService);
    }

    @Test
    void testGetAvailableDriver_ShouldReturnResponse_Valid() {
        rideResponse = TestDataUtil.getRideResponse(null);
        when(rideService.getAvailableDriver(TestDataUtil.RIDE_ID)).thenReturn(rideResponse);

        RideResponse result = rideController.getAvailableDriver(TestDataUtil.RIDE_ID);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(rideService).getAvailableDriver(TestDataUtil.RIDE_ID);
        verifyNoMoreInteractions(rideService);
    }

    @Test
    void testChangeRideStatus_ShouldReturnResponse_Valid() {
        rideResponse = TestDataUtil.getRideResponse(RideStatus.COMPLETED);
        when(rideService.changeRideStatus(TestDataUtil.RIDE_ID, RideStatus.COMPLETED)).thenReturn(rideResponse);

        RideResponse result = rideController.changeRideStatus(TestDataUtil.RIDE_ID, RideStatus.COMPLETED);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(rideService).changeRideStatus(TestDataUtil.RIDE_ID, RideStatus.COMPLETED);
        verifyNoMoreInteractions(rideService);
    }

    @Test
    void testGetRide_ShouldReturnResponse_Valid() {
        rideResponse = TestDataUtil.getRideResponse(null);
        when(rideService.getRide(TestDataUtil.RIDE_ID)).thenReturn(rideResponse);

        RideResponse result = rideController.getRide(TestDataUtil.RIDE_ID);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(rideService).getRide(TestDataUtil.RIDE_ID);
        verifyNoMoreInteractions(rideService);
    }

    @Test
    void testGetAllRides_ShouldReturnPageResponse_Valid() {
        rideResponse = TestDataUtil.getRideResponse(null);
        List<RideResponse> rideResponseList = List.of(rideResponse);
        PageResponse<RideResponse> pageResponse = TestDataUtil.getPageResponse(rideResponseList);
        when(rideService.getAllRides(TestDataUtil.PAGE, TestDataUtil.SIZE)).thenReturn(pageResponse);

        PageResponse<RideResponse> result = rideController.getAllRides(TestDataUtil.PAGE, TestDataUtil.SIZE);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(pageResponse, result);
        });
        verify(rideService).getAllRides(TestDataUtil.PAGE, TestDataUtil.SIZE);
    }
}
