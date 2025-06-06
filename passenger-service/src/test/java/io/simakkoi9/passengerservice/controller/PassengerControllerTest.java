package io.simakkoi9.passengerservice.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.service.PassengerService;
import io.simakkoi9.passengerservice.util.TestDataUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassengerControllerTest {
    @Mock
    private PassengerService passengerService;

    @InjectMocks
    private PassengerController passengerController;

    Long id;
    PassengerCreateRequest passengerCreateRequest;
    PassengerUpdateRequest passengerUpdateRequest;
    PassengerResponse passengerResponse;
    PassengerResponse updatedPassengerResponse;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createPassenger_ShouldCallServiceAndReturnResponse() {
        passengerCreateRequest = TestDataUtil.CREATE_REQUEST;
        passengerResponse = TestDataUtil.RESPONSE;
        when(passengerService.createPassenger(passengerCreateRequest)).thenReturn(passengerResponse);

        PassengerResponse result = passengerController.createPassenger(passengerCreateRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(passengerResponse, result)
        );
        verify(passengerService).createPassenger(passengerCreateRequest);
        verifyNoMoreInteractions(passengerService);
    }

    @Test
    void updatePassenger_ShouldCallServiceAndReturnUpdatedResponse() {
        id = TestDataUtil.ID;
        passengerUpdateRequest = TestDataUtil.UPDATE_REQUEST;
        updatedPassengerResponse = TestDataUtil.UPDATED_RESPONSE;
        when(passengerService.updatePassenger(id, passengerUpdateRequest)).thenReturn(updatedPassengerResponse);

        PassengerResponse result = passengerController.updatePassenger(id, passengerUpdateRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedPassengerResponse, result)
        );
        verify(passengerService).updatePassenger(id, passengerUpdateRequest);
        verifyNoMoreInteractions(passengerService);
    }

    @Test
    void deletePassenger_ShouldCallServiceAndReturnDeletedResponse() {
        id = TestDataUtil.ID;
        passengerResponse = TestDataUtil.RESPONSE;
        when(passengerService.deletePassenger(id)).thenReturn(passengerResponse);

        PassengerResponse result = passengerController.deletePassenger(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(passengerResponse, result)
        );
        verify(passengerService).deletePassenger(id);
        verifyNoMoreInteractions(passengerService);
    }

    @Test
    void getPassenger_ShouldCallServiceAndReturnPassengerResponse() {
        id = TestDataUtil.ID;
        passengerResponse = TestDataUtil.RESPONSE;
        when(passengerService.getPassenger(id)).thenReturn(passengerResponse);

        PassengerResponse result = passengerController.getPassenger(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(passengerResponse, result)
        );
        verify(passengerService).getPassenger(id);
        verifyNoMoreInteractions(passengerService);
    }

    @Test
    void getAllPassengers_ShouldCallServiceAndReturnPageResponse() {
        passengerResponse = TestDataUtil.RESPONSE;
        PageResponse<PassengerResponse> pageResponse = TestDataUtil.getPageResponse(List.of(passengerResponse));
        when(passengerService.getAllPassengers(TestDataUtil.PAGE, TestDataUtil.SIZE)).thenReturn(pageResponse);

        PageResponse<PassengerResponse> result = passengerController
                .getAllPassengers(TestDataUtil.PAGE, TestDataUtil.SIZE);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(pageResponse, result)
        );
        verify(passengerService).getAllPassengers(TestDataUtil.PAGE, TestDataUtil.SIZE);
        verifyNoMoreInteractions(passengerService);
    }

}
