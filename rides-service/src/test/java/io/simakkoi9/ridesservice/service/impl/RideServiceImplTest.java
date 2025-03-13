package io.simakkoi9.ridesservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.simakkoi9.ridesservice.client.PassengerClient;
import io.simakkoi9.ridesservice.exception.BusyPassengerException;
import io.simakkoi9.ridesservice.exception.InvalidStatusException;
import io.simakkoi9.ridesservice.exception.RideNotFoundException;
import io.simakkoi9.ridesservice.model.dto.feign.PassengerRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.rest.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.model.mapper.PassengerMapper;
import io.simakkoi9.ridesservice.model.mapper.RideMapper;
import io.simakkoi9.ridesservice.repository.RideRepository;
import io.simakkoi9.ridesservice.service.FareService;
import io.simakkoi9.ridesservice.util.TestDataUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {
    @Mock
    RideMapper mapper;
    @Mock
    PassengerMapper passengerMapper;
    @Mock
    RideRepository repository;
    @Mock
    FareService fareService;
    @Mock
    PassengerClient passengerClient;
    @InjectMocks
    RideServiceImpl rideServiceImpl;

    private RideCreateRequest rideCreateRequest;
    private Ride ride;
    private PassengerRequest passengerRequest;
    private Passenger passenger;
    private RideResponse rideResponse;
    private RideUpdateRequest rideUpdateRequest;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testCreateRide_ShouldReturnResponse_Valid() {
        rideCreateRequest = TestDataUtil.getRideCreateRequest();
        ride = TestDataUtil.getRide();
        passengerRequest = TestDataUtil.getPassengerRequest();
        passenger = TestDataUtil.getPassenger();
        rideResponse = TestDataUtil.getRideResponse(null);
        when(mapper.toEntity(rideCreateRequest)).thenReturn(ride);
        when(
                repository.existsByPassenger_IdAndStatusIn(
                        TestDataUtil.PASSENGER_ID,
                        RideStatus.getBusyPassengerStatusList()
                )
        ).thenReturn(false);
        when(passengerClient.getPassengerById(TestDataUtil.PASSENGER_ID)).thenReturn(passengerRequest);
        when(passengerMapper.toEntity(passengerRequest)).thenReturn(passenger);
        when(fareService.calculateFare(TestDataUtil.PICKUP_ADDRESS, TestDataUtil.DESTINATION_ADDRESS))
                .thenReturn(Mono.just(TestDataUtil.COST));
        when(repository.save(ride)).thenReturn(ride);
        when(mapper.toResponse(ride)).thenReturn(rideResponse);

        RideResponse result = rideServiceImpl.createRide(rideCreateRequest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(mapper).toEntity(rideCreateRequest);
        verify(repository).existsByPassenger_IdAndStatusIn(
                TestDataUtil.PASSENGER_ID, RideStatus.getBusyPassengerStatusList()
        );
        verify(passengerClient).getPassengerById(TestDataUtil.PASSENGER_ID);
        verify(fareService).calculateFare(TestDataUtil.PICKUP_ADDRESS, TestDataUtil.DESTINATION_ADDRESS);
        verify(repository).save(ride);
        verify(mapper).toResponse(ride);
        verifyNoMoreInteractions(mapper, repository, fareService, passengerClient);
    }

    @Test
    void testCreateRide_ShouldThrowBusyPassengerException() {
        rideCreateRequest = TestDataUtil.getRideCreateRequest();
        ride = TestDataUtil.getRide();
        when(mapper.toEntity(rideCreateRequest)).thenReturn(ride);
        when(repository.existsByPassenger_IdAndStatusIn(
                    TestDataUtil.PASSENGER_ID, RideStatus.getBusyPassengerStatusList()
                )
        ).thenReturn(true);

        assertThrows(BusyPassengerException.class, () -> {
            rideServiceImpl.createRide(rideCreateRequest);
        });
        verify(mapper).toEntity(rideCreateRequest);
        verify(repository).existsByPassenger_IdAndStatusIn(
                TestDataUtil.PASSENGER_ID, RideStatus.getBusyPassengerStatusList()
        );
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void testUpdateRide_ShouldReturnResponse_Valid() {
        ride = TestDataUtil.getRide();
        rideUpdateRequest = TestDataUtil.getRideUpdateRequest();
        rideResponse = TestDataUtil.getRideResponse(null);
        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));
        doAnswer(invocation -> {
            RideUpdateRequest request = invocation.getArgument(0);
            Ride ride = invocation.getArgument(1);
            ride.setPickupAddress(request.pickupAddress());
            ride.setDestinationAddress(request.destinationAddress());
            return null;
        }).when(mapper).partialUpdate(rideUpdateRequest, ride);
        when(fareService.calculateFare(TestDataUtil.PICKUP_ADDRESS_2, TestDataUtil.DESTINATION_ADDRESS_2))
                .thenReturn(Mono.just(TestDataUtil.COST_2));
        when(repository.save(ride)).thenReturn(ride);
        when(mapper.toResponse(ride)).thenReturn(rideResponse);

        RideResponse result = rideServiceImpl.updateRide(TestDataUtil.RIDE_ID, rideUpdateRequest);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(repository).findById(TestDataUtil.RIDE_ID);
        verify(mapper).partialUpdate(rideUpdateRequest, ride);
        verify(fareService).calculateFare(TestDataUtil.PICKUP_ADDRESS_2, TestDataUtil.DESTINATION_ADDRESS_2);
        verify(repository).save(ride);
        verify(mapper).toResponse(ride);
        verifyNoMoreInteractions(mapper, repository, fareService);
    }

    @Test
    void testChangeRideStatus_ShouldReturnResponse_WhenNewStatusGreaterByOne() {
        ride = TestDataUtil.getRide();
        ride.setStatus(RideStatus.EN_ROUTE_TO_PASSENGER);
        rideResponse = TestDataUtil.getRideResponse(RideStatus.EN_ROUTE_TO_DESTINATION);

        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));
        when(repository.save(ride)).thenReturn(ride);
        when(mapper.toResponse(ride)).thenReturn(rideResponse);

        RideResponse result = rideServiceImpl.changeRideStatus(
                TestDataUtil.RIDE_ID,
                RideStatus.EN_ROUTE_TO_DESTINATION
        );

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(repository).findById(TestDataUtil.RIDE_ID);
        verify(repository).save(ride);
        verify(mapper).toResponse(ride);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void testChangeRideStatus_ShouldReturnResponse_WhenNewStatusIsImmutable() {
        ride = TestDataUtil.getRide();
        ride.setStatus(RideStatus.CREATED);
        rideResponse = TestDataUtil.getRideResponse(RideStatus.CANCELLED_BY_PASSENGER);

        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));
        when(repository.save(ride)).thenReturn(ride);
        when(mapper.toResponse(ride)).thenReturn(rideResponse);

        RideResponse result = rideServiceImpl.changeRideStatus(TestDataUtil.RIDE_ID, RideStatus.CANCELLED_BY_PASSENGER);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(repository).findById(TestDataUtil.RIDE_ID);
        verify(repository).save(ride);
        verify(mapper).toResponse(ride);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void testChangeRideStatus_ShouldThrowException_WhenOldStatusIsImmutable() {
        ride = TestDataUtil.getRide();
        ride.setStatus(RideStatus.COMPLETED);

        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));

        assertThrows(InvalidStatusException.class, () ->
                rideServiceImpl.changeRideStatus(TestDataUtil.RIDE_ID, RideStatus.EN_ROUTE_TO_PASSENGER)
        );

        verify(repository).findById(TestDataUtil.RIDE_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testChangeRideStatus_ShouldThrowException_WhenNewStatusNotImmutableAndDifferenceMoreThanOne() {
        ride = TestDataUtil.getRide();
        ride.setStatus(RideStatus.CREATED);

        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));

        assertThrows(InvalidStatusException.class, () ->
                rideServiceImpl.changeRideStatus(TestDataUtil.RIDE_ID, RideStatus.EN_ROUTE_TO_DESTINATION)
        );

        verify(repository).findById(TestDataUtil.RIDE_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testChangeRideStatus_ShouldThrowException_WhenOldStatusGreaterThanNew() {
        ride = TestDataUtil.getRide();
        ride.setStatus(RideStatus.EN_ROUTE_TO_DESTINATION);

        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));

        assertThrows(InvalidStatusException.class, () ->
                rideServiceImpl.changeRideStatus(TestDataUtil.RIDE_ID, RideStatus.EN_ROUTE_TO_PASSENGER)
        );

        verify(repository).findById(TestDataUtil.RIDE_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetRide_ShouldReturnResponse_Valid() {
        ride = TestDataUtil.getRide();
        rideResponse = TestDataUtil.getRideResponse(null);

        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));
        when(mapper.toResponse(ride)).thenReturn(rideResponse);

        RideResponse result = rideServiceImpl.getRide(TestDataUtil.RIDE_ID);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(rideResponse, result);
        });
        verify(repository).findById(TestDataUtil.RIDE_ID);
        verify(mapper).toResponse(ride);
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void testGetRide_ShouldThrowNotFoundException() {
        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> {
            rideServiceImpl.getRide(TestDataUtil.RIDE_ID);
        });
        verify(repository).findById(TestDataUtil.RIDE_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetAllRides_ShouldReturnPageResponse_Valid() {
        ride = TestDataUtil.getRide();
        List<Ride> rideList = List.of(ride);
        rideResponse = TestDataUtil.getRideResponse(null);
        List<RideResponse> rideResponseList = List.of(rideResponse);
        var ridePage = new PageImpl<>(
                rideList,
                TestDataUtil.getPageRequest(),
                TestDataUtil.TOTAL_ELEMENTS
        );
        PageResponse<RideResponse> pageResponse = TestDataUtil.getPageResponse(rideResponseList);
        when(repository.findAll(TestDataUtil.getPageRequest())).thenReturn(ridePage);
        when(mapper.toPageResponse(ridePage)).thenReturn(pageResponse);

        PageResponse<RideResponse> result = rideServiceImpl.getAllRides(TestDataUtil.PAGE, TestDataUtil.SIZE);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(pageResponse, result);
        });
        verify(repository).findAll(TestDataUtil.getPageRequest());
        verify(mapper).toPageResponse(ridePage);
        verifyNoMoreInteractions(mapper, repository);
    }

    @ParameterizedTest
    @MethodSource("providePersons")
    void testGetRidePersonId_ShouldReturnPersonId_Valid(String person, String personId) {
        ride = TestDataUtil.getRide();
        ride.setPassenger(TestDataUtil.getPassenger());
        ride.setDriver(TestDataUtil.getDriver());
        when(repository.findById(TestDataUtil.RIDE_ID)).thenReturn(Optional.of(ride));

        String result = rideServiceImpl.getRidePersonId(TestDataUtil.RIDE_ID, person);

        assertAll(() -> {
            assertNotNull(result);
            assertEquals(personId, result);
        });
        verify(repository, times(2)).findById(TestDataUtil.RIDE_ID);
        verifyNoMoreInteractions(repository);
    }

    private static Stream<Arguments> providePersons() {
        return Stream.of(
                Arguments.of(
                        TestDataUtil.PERSON_PASSENGER,
                        TestDataUtil.PERSON_PASSENGER_ID
                ),
                Arguments.of(
                        TestDataUtil.PERSON_DRIVER,
                        TestDataUtil.PERSON_DRIVER_ID
                )
        );
    }
}
