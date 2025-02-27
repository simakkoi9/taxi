package io.simakkoi9.passengerservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import io.simakkoi9.passengerservice.model.mapper.PassengerMapper;
import io.simakkoi9.passengerservice.repository.PassengerRepository;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {
    @Mock
    PassengerMapper mapper;
    @Mock
    PassengerRepository repository;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    PassengerServiceImpl passengerServiceImpl;

    Passenger passenger;
    PassengerCreateRequest passengerCreateRequest;
    PassengerResponse passengerResponse;

    @BeforeEach
    void setUp() {
        passengerCreateRequest = new PassengerCreateRequest(
                "name",
                "email@mail.com",
                "+375293453434"
        );

        passengerResponse = new PassengerResponse(
                1L,
                "name",
                "email@mail.com",
                "+375293453434",
                LocalDateTime.now()
        );

        passenger = new Passenger(
                1L,
                "name",
                "email@mail.com",
                "+375293453434",
                UserStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    @Test
    void testCreatePassenger_ThenReturnResponse_Valid() {
        when(repository.existsByEmailAndStatus(passengerCreateRequest.email(), UserStatus.ACTIVE))
                .thenReturn(false);
        when(mapper.toEntity(passengerCreateRequest)).thenReturn(passenger);
        when(repository.save(passenger)).thenReturn(passenger);
        when(mapper.toResponse(passenger)).thenReturn(passengerResponse);

        PassengerResponse result = passengerServiceImpl.createPassenger(passengerCreateRequest);

        assertNotNull(result);
        assertEquals(passengerResponse, result);
        verify(repository).save(passenger);
        verify(mapper).toResponse(passenger);
    }

    @Test
    void testUpdatePassenger() {
        when(mapper.toResponse(any())).thenReturn(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18)));
        when(repository.findByIdAndStatus(anyLong(), any())).thenReturn(null);

        PassengerResponse result = passengerServiceImpl.updatePassenger(Long.valueOf(1), new PassengerUpdateRequest("name", "email", "phone"));
        assertEquals(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18)), result);
    }

    @Test
    void testDeletePassenger() {
        when(mapper.toResponse(any())).thenReturn(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18)));
        when(repository.findByIdAndStatus(anyLong(), any())).thenReturn(null);

        PassengerResponse result = passengerServiceImpl.deletePassenger(Long.valueOf(1));
        assertEquals(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18)), result);
    }

    @Test
    void testGetPassenger() {
        when(mapper.toResponse(any())).thenReturn(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18)));
        when(repository.findByIdAndStatus(anyLong(), any())).thenReturn(null);

        PassengerResponse result = passengerServiceImpl.getPassenger(Long.valueOf(1));
        assertEquals(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18)), result);
    }

    @Test
    void testGetAllPassengers() {
        when(mapper.toPageResponse(any())).thenReturn(new PageResponse<PassengerResponse>(List.of(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18))), 0, 0, 0, 0L));
        when(repository.findAllByStatus(any(), any())).thenReturn(null);

        PageResponse<PassengerResponse> result = passengerServiceImpl.getAllPassengers(0, 0);
        assertEquals(new PageResponse<PassengerResponse>(List.of(new PassengerResponse(Long.valueOf(1), "name", "email", "phone", LocalDateTime.of(2025, Month.FEBRUARY, 28, 12, 27, 18))), 0, 0, 0, 0L), result);
    }
}
