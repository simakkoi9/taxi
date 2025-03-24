package io.simakkoi9.passengerservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.simakkoi9.passengerservice.exception.DuplicatePassengerFoundException;
import io.simakkoi9.passengerservice.exception.PassengerNotFoundException;
import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import io.simakkoi9.passengerservice.model.mapper.PassengerMapper;
import io.simakkoi9.passengerservice.repository.PassengerRepository;
import io.simakkoi9.passengerservice.util.MessageKeyConstants;
import io.simakkoi9.passengerservice.util.TestDataUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    Long id;
    Passenger passenger;
    PassengerCreateRequest passengerCreateRequest;
    PassengerUpdateRequest passengerUpdateRequest;
    PassengerResponse passengerResponse;
    PassengerResponse updatedPassengerResponse;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testCreatePassenger_ShouldReturnResponse_Valid() {
        passengerCreateRequest = TestDataUtil.CREATE_REQUEST;
        passengerResponse = TestDataUtil.RESPONSE;
        passenger = TestDataUtil.getPassenger();
        when(repository.existsByEmailAndStatus(passengerCreateRequest.email(), UserStatus.ACTIVE))
                .thenReturn(false);
        when(mapper.toEntity(passengerCreateRequest)).thenReturn(passenger);
        when(repository.save(passenger)).thenReturn(passenger);
        when(mapper.toResponse(passenger)).thenReturn(passengerResponse);

        PassengerResponse result = passengerServiceImpl.createPassenger(passengerCreateRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(passengerResponse, result)
        );
        verify(repository).existsByEmailAndStatus(passengerCreateRequest.email(), UserStatus.ACTIVE);
        verify(repository).save(passenger);
        verify(mapper).toResponse(passenger);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void testCreatePassenger_ShouldThrowDuplicatePassengerFoundException() {
        passengerCreateRequest = TestDataUtil.CREATE_REQUEST;
        when(repository.existsByEmailAndStatus(passengerCreateRequest.email(), UserStatus.ACTIVE))
                .thenReturn(true);
        String expectedMessage = TestDataUtil.getDuplicatePassengerErrorMessage(passengerCreateRequest.email());
        when(
                messageSource.getMessage(
                        MessageKeyConstants.DUPLICATE_PASSENGER_FOUND,
                        new Object[]{passengerCreateRequest.email()},
                        LocaleContextHolder.getLocale()
                )
        ).thenReturn(expectedMessage);

        Exception exception = assertThrows(
                DuplicatePassengerFoundException.class,
                () -> passengerServiceImpl.createPassenger(passengerCreateRequest)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository).existsByEmailAndStatus(passengerCreateRequest.email(), UserStatus.ACTIVE);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testUpdatePassenger_ShouldReturnResponse_Valid() {
        id = TestDataUtil.ID;
        passengerUpdateRequest = TestDataUtil.UPDATE_REQUEST;
        passenger = TestDataUtil.getPassenger();
        updatedPassengerResponse = TestDataUtil.UPDATED_RESPONSE;
        when(repository.findByIdAndStatus(id, UserStatus.ACTIVE)).thenReturn(Optional.of(passenger));
        doNothing().when(mapper).setPassengerUpdateRequest(passengerUpdateRequest, passenger);
        when(repository.save(passenger)).thenReturn(passenger);
        when(mapper.toResponse(passenger)).thenReturn(updatedPassengerResponse);

        PassengerResponse result = passengerServiceImpl.updatePassenger(id, passengerUpdateRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedPassengerResponse, result)
        );
        verify(repository).findByIdAndStatus(id, UserStatus.ACTIVE);
        verify(mapper).setPassengerUpdateRequest(passengerUpdateRequest, passenger);
        verify(repository).save(passenger);
        verify(mapper).toResponse(passenger);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void testDeletePassenger_ShouldReturnResponse_Valid() {
        id = TestDataUtil.ID;
        passengerResponse = TestDataUtil.RESPONSE;
        passenger = TestDataUtil.getPassenger();
        when(repository.findByIdAndStatus(id, UserStatus.ACTIVE)).thenReturn(Optional.of(passenger));
        when(repository.save(passenger)).thenReturn(passenger);
        when(mapper.toResponse(passenger)).thenReturn(passengerResponse);

        PassengerResponse result = passengerServiceImpl.deletePassenger(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(UserStatus.DELETED, passenger.getStatus())
        );
        verify(repository).findByIdAndStatus(id, UserStatus.ACTIVE);
        verify(repository).save(passenger);
        verify(mapper).toResponse(passenger);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void testGetPassenger_ShouldReturnResponse_Valid() {
        id = TestDataUtil.ID;
        passengerResponse = TestDataUtil.RESPONSE;
        passenger = TestDataUtil.getPassenger();
        when(repository.findByIdAndStatus(id, UserStatus.ACTIVE)).thenReturn(Optional.of(passenger));
        when(mapper.toResponse(passenger)).thenReturn(passengerResponse);

        PassengerResponse result = passengerServiceImpl.getPassenger(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(passengerResponse, result)
        );
        verify(repository).findByIdAndStatus(id, UserStatus.ACTIVE);
        verify(mapper).toResponse(passenger);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void testGetPassenger_ShouldThrowPassengerNotFoundException() {
        when(repository.findByIdAndStatus(TestDataUtil.INVALID_ID, UserStatus.ACTIVE)).thenReturn(Optional.empty());
        String expectedMessage = TestDataUtil.getPassengerNotFoundErrorMessage(TestDataUtil.INVALID_ID);
        when(messageSource.getMessage(
                MessageKeyConstants.PASSENGER_NOT_FOUND,
                new Object[]{TestDataUtil.INVALID_ID.toString()},
                LocaleContextHolder.getLocale()
            )
        ).thenReturn(expectedMessage);

        Exception exception = assertThrows(
                    PassengerNotFoundException.class,
                    () -> passengerServiceImpl.getPassenger(TestDataUtil.INVALID_ID)
                );

        assertEquals(expectedMessage, exception.getMessage());
        verify(repository).findByIdAndStatus(TestDataUtil.INVALID_ID, UserStatus.ACTIVE);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAllPassengers_ShouldReturnPageResponse_ValidPageValues() {
        passengerResponse = TestDataUtil.RESPONSE;
        passenger = TestDataUtil.getPassenger();
        PageRequest pageRequest = PageRequest.of(TestDataUtil.PAGE, TestDataUtil.SIZE);
        List<Passenger> passengerList = List.of(passenger);
        Page<Passenger> passengerPage = new PageImpl<>(passengerList, pageRequest, passengerList.size());
        PageResponse<PassengerResponse> pageResponse = TestDataUtil.getPageResponse(List.of(passengerResponse));

        when(repository.findAllByStatus(UserStatus.ACTIVE, pageRequest)).thenReturn(passengerPage);
        when(mapper.toPageResponse(passengerPage)).thenReturn(pageResponse);

        PageResponse<PassengerResponse> result = passengerServiceImpl
                .getAllPassengers(TestDataUtil.PAGE, TestDataUtil.SIZE);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(TestDataUtil.TOTAL_ELEMENTS, result.content().size())
        );
        verify(repository).findAllByStatus(UserStatus.ACTIVE, pageRequest);
        verify(mapper).toPageResponse(passengerPage);
        verifyNoMoreInteractions(repository, mapper);
    }

}
