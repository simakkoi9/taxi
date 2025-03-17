package io.simakkoi9.ratingservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.simakkoi9.ratingservice.annotation.TransactionalQuarkusTest;
import io.simakkoi9.ratingservice.client.RidesClient;
import io.simakkoi9.ratingservice.config.message.MessageConfig;
import io.simakkoi9.ratingservice.exception.DriverAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.DuplicateRatingException;
import io.simakkoi9.ratingservice.exception.NoRatesException;
import io.simakkoi9.ratingservice.exception.PassengerAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.RatingNotFoundException;
import io.simakkoi9.ratingservice.exception.UncompletedRideException;
import io.simakkoi9.ratingservice.model.dto.kafka.RidePersonRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingPageResponse;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingResponse;
import io.simakkoi9.ratingservice.model.entity.Rate;
import io.simakkoi9.ratingservice.model.entity.Rating;
import io.simakkoi9.ratingservice.model.mapper.RatingMapper;
import io.simakkoi9.ratingservice.repository.RateRepository;
import io.simakkoi9.ratingservice.repository.RatingRepository;
import io.simakkoi9.ratingservice.util.MessageKeyConstants;
import io.simakkoi9.ratingservice.util.TestDataUtil;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import java.util.List;
import java.util.Optional;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TransactionalQuarkusTest
class RatingServiceImplTest {

    @InjectMock
    RatingMapper ratingMapper;

    @InjectMock
    RatingRepository ratingRepository;

    @InjectMock
    RateRepository rateRepository;

    @InjectMock
    MessageConfig messageConfig;

    @RestClient
    @InjectMock
    RidesClient ridesClient;

    @Mock
    Emitter<KafkaRecord<String, RidePersonRequest>> emitter;

    @InjectMock
    TransactionSynchronizationRegistry txSyncRegistry;

    private ArgumentCaptor<Synchronization> synchronizationCaptor;
    private ArgumentCaptor<KafkaRecord<String, RidePersonRequest>> kafkaRecordCaptor;

    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PanacheMock.mock(Rating.class);
        PanacheMock.mock(Rate.class);
        synchronizationCaptor = ArgumentCaptor.forClass(Synchronization.class);
        kafkaRecordCaptor = ArgumentCaptor.forClass(KafkaRecord.class);
        
        ratingService = new RatingServiceImpl();
        ratingService.ratingMapper = ratingMapper;
        ratingService.ratingRepository = ratingRepository;
        ratingService.rateRepository = rateRepository;
        ratingService.messageConfig = messageConfig;
        ratingService.ridesClient = ridesClient;
        ratingService.emitter = emitter;
        ratingService.txSyncRegistry = txSyncRegistry;
        ratingService.limit = 10;
    }

    @Test
    void testCreateRating() {
        RatingCreateRequest request = TestDataUtil.createRatingRequest();
        Rating rating = TestDataUtil.createNewRating();
        Rating savedRating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(savedRating);

        when(ratingRepository.existsByRideId(TestDataUtil.RIDE_ID)).thenReturn(false);
        when(ridesClient.getRideById(TestDataUtil.RIDE_ID)).thenReturn(TestDataUtil.createCompletedRideJson());
        when(ratingMapper.toEntity(request)).thenReturn(rating);
        when(ratingMapper.toResponse(any(Rating.class))).thenReturn(expectedResponse);

        RatingResponse result = ratingService.createRating(request);

        assertEquals(expectedResponse, result);
        
        verify(ratingRepository).existsByRideId(TestDataUtil.RIDE_ID);
        verify(ridesClient).getRideById(TestDataUtil.RIDE_ID);
        verify(ratingMapper).toEntity(request);
        verify(ratingMapper).toResponse(any(Rating.class));
    }

    @Test
    void testCreateRating_DuplicateRating() {
        RatingCreateRequest request = TestDataUtil.createRatingRequest();
        
        when(ratingRepository.existsByRideId(TestDataUtil.RIDE_ID)).thenReturn(true);
        when(messageConfig.getMessage(MessageKeyConstants.DUPLICATE_RATING))
                .thenReturn(TestDataUtil.ERROR_DUPLICATE_RATING);
        
        assertThrows(DuplicateRatingException.class, () -> ratingService.createRating(request));
        
        verify(ratingRepository).existsByRideId(TestDataUtil.RIDE_ID);
        verify(messageConfig).getMessage(MessageKeyConstants.DUPLICATE_RATING);
    }

    @Test
    void testCreateRating_UncompletedRide() {
        RatingCreateRequest request = TestDataUtil.createRatingRequest();
        
        when(ratingRepository.existsByRideId(TestDataUtil.RIDE_ID)).thenReturn(false);
        when(ridesClient.getRideById(TestDataUtil.RIDE_ID)).thenReturn(TestDataUtil.createUncompletedRideJson());
        when(messageConfig.getMessage(MessageKeyConstants.UNCOMPLETED_RIDE))
                .thenReturn(TestDataUtil.ERROR_UNCOMPLETED_RIDE);
        
        assertThrows(UncompletedRideException.class,
                () -> ratingService.createRating(request));
        
        verify(ratingRepository).existsByRideId(TestDataUtil.RIDE_ID);
        verify(ridesClient).getRideById(TestDataUtil.RIDE_ID);
        verify(messageConfig).getMessage(MessageKeyConstants.UNCOMPLETED_RIDE);
    }

    @Test
    void testSetRateForDriver() {
        DriverRatingUpdateRequest request = TestDataUtil.createDriverRatingUpdateRequest();
        Rating rating = TestDataUtil.createRatingWithoutDriverRate();
        Rating updatedRating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(updatedRating);
        
        when(Rating.findByIdOptional(TestDataUtil.RATING_ID)).thenReturn(Optional.of(rating));
        when(ratingMapper.driverRatingPartialUpdate(request, rating)).thenReturn(updatedRating);
        when(ratingMapper.toResponse(updatedRating)).thenReturn(expectedResponse);
        
        RatingResponse result = ratingService.setRateForDriver(TestDataUtil.RATING_ID, request);
        
        assertEquals(expectedResponse, result);
        
        PanacheMock.verify(Rating.class).findByIdOptional(TestDataUtil.RATING_ID);
        verify(ratingMapper).driverRatingPartialUpdate(request, rating);
        verify(ratingMapper).toResponse(updatedRating);
        verify(txSyncRegistry).registerInterposedSynchronization(any(Synchronization.class));
    }

    @Test
    void testSetRateForDriver_AlreadyRated() {
        DriverRatingUpdateRequest request = TestDataUtil.createDriverRatingUpdateRequest();
        Rating rating = TestDataUtil.createRating();
        
        when(Rating.findByIdOptional(TestDataUtil.RATING_ID)).thenReturn(Optional.of(rating));
        when(messageConfig.getMessage(MessageKeyConstants.DRIVER_ALREADY_RATED))
                .thenReturn(TestDataUtil.ERROR_DRIVER_ALREADY_RATED);
        
        assertThrows(DriverAlreadyRatedException.class,
                () -> ratingService.setRateForDriver(TestDataUtil.RATING_ID, request));
        
        PanacheMock.verify(Rating.class).findByIdOptional(TestDataUtil.RATING_ID);
        verify(messageConfig).getMessage(MessageKeyConstants.DRIVER_ALREADY_RATED);
    }

    @Test
    void testSetRateForPassenger() {
        PassengerRatingUpdateRequest request = TestDataUtil.createPassengerRatingUpdateRequest();
        Rating rating = TestDataUtil.createRatingWithoutPassengerRate();
        Rating updatedRating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(updatedRating);
        
        when(Rating.findByIdOptional(TestDataUtil.RATING_ID)).thenReturn(Optional.of(rating));
        when(ratingMapper.passengerRatingPartialUpdate(request, rating)).thenReturn(updatedRating);
        when(ratingMapper.toResponse(updatedRating)).thenReturn(expectedResponse);
        
        RatingResponse result = ratingService.setRateForPassenger(TestDataUtil.RATING_ID, request);
        
        assertEquals(expectedResponse, result);
        
        PanacheMock.verify(Rating.class).findByIdOptional(TestDataUtil.RATING_ID);
        verify(ratingMapper).passengerRatingPartialUpdate(request, rating);
        verify(ratingMapper).toResponse(updatedRating);
        verify(txSyncRegistry).registerInterposedSynchronization(any(Synchronization.class));
    }

    @Test
    void testSetRateForPassenger_PassengerAlreadyRated() {
        PassengerRatingUpdateRequest request = TestDataUtil.createPassengerRatingUpdateRequest();
        Rating rating = TestDataUtil.createRating();
        
        when(Rating.findByIdOptional(TestDataUtil.RATING_ID)).thenReturn(Optional.of(rating));
        when(messageConfig.getMessage(MessageKeyConstants.PASSENGER_ALREADY_RATED))
                .thenReturn(TestDataUtil.ERROR_PASSENGER_ALREADY_RATED);
        
        assertThrows(PassengerAlreadyRatedException.class,
                () -> ratingService.setRateForPassenger(TestDataUtil.RATING_ID, request));
        
        PanacheMock.verify(Rating.class).findByIdOptional(TestDataUtil.RATING_ID);
        verify(messageConfig).getMessage(MessageKeyConstants.PASSENGER_ALREADY_RATED);
    }

    @Test
    void testGetRating() {
        Rating rating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(rating);
        
        when(Rating.findByIdOptional(TestDataUtil.RATING_ID)).thenReturn(Optional.of(rating));
        when(ratingMapper.toResponse(rating)).thenReturn(expectedResponse);
        
        RatingResponse result = ratingService.getRating(TestDataUtil.RATING_ID);
        
        assertEquals(expectedResponse, result);
        
        PanacheMock.verify(Rating.class).findByIdOptional(TestDataUtil.RATING_ID);
        verify(ratingMapper).toResponse(rating);
    }

    @Test
    void testGetRating_NotFound() {
        when(Rating.findByIdOptional(TestDataUtil.RATING_ID)).thenReturn(Optional.empty());
        when(messageConfig.getMessage(MessageKeyConstants.RATING_NOT_FOUND))
                .thenReturn(TestDataUtil.ERROR_RATING_NOT_FOUND);
        
        assertThrows(RatingNotFoundException.class,
                () -> ratingService.getRating(TestDataUtil.RATING_ID));
        
        PanacheMock.verify(Rating.class).findByIdOptional(TestDataUtil.RATING_ID);
        verify(messageConfig).getMessage(MessageKeyConstants.RATING_NOT_FOUND);
    }

    @Test
    void testGetAllRatings() {
        List<Rating> ratings = List.of(TestDataUtil.createRating());
        List<RatingResponse> ratingResponses = List.of(TestDataUtil.createRatingResponse(ratings.get(0)));
        PanacheQuery panacheQuery = mock(PanacheQuery.class);
        
        when(Rating.findAll()).thenReturn(panacheQuery);
        when(panacheQuery.page(any(Page.class))).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(ratings);
        when(panacheQuery.pageCount()).thenReturn(1);
        when(ratingMapper.toResponseList(ratings)).thenReturn(ratingResponses);
        
        RatingPageResponse result = ratingService.getAllRatings(TestDataUtil.PAGE, TestDataUtil.SIZE);
        
        assertEquals(ratingResponses, result.content());
        assertEquals(TestDataUtil.PAGE, result.currentPage());
        assertEquals(TestDataUtil.SIZE, result.size());
        assertEquals(1, result.totalPages());
        
        PanacheMock.verify(Rating.class).findAll();
        verify(panacheQuery).page(any(Page.class));
        verify(panacheQuery).list();
        verify(panacheQuery).pageCount();
        verify(ratingMapper).toResponseList(ratings);
    }

    @Test
    void testGetAverageDriverRating() {
        List<Rate> driverRates = TestDataUtil.createDriverRates();
        Double expectedAverage = 4.5;
        
        when(rateRepository.getLastRatesByPersonId(TestDataUtil.DRIVER_PERSON_ID, 10))
                .thenReturn(driverRates);
        
        AverageRatingResponse result = ratingService.getAverageDriverRating(TestDataUtil.DRIVER_ID);
        
        assertEquals(TestDataUtil.DRIVER_PERSON_ID, result.personId());
        assertEquals(expectedAverage, result.averageRating());
        
        verify(rateRepository).getLastRatesByPersonId(TestDataUtil.DRIVER_PERSON_ID, 10);
    }

    @Test
    void testGetAverageDriverRating_NoRates() {
        when(rateRepository.getLastRatesByPersonId(TestDataUtil.DRIVER_PERSON_ID, 10))
                .thenReturn(List.of());
        when(messageConfig.getMessage(MessageKeyConstants.DRIVER_NO_RATES))
                .thenReturn(TestDataUtil.ERROR_DRIVER_NO_RATES);
        
        assertThrows(NoRatesException.class,
                () -> ratingService.getAverageDriverRating(TestDataUtil.DRIVER_ID));
        
        verify(rateRepository).getLastRatesByPersonId(TestDataUtil.DRIVER_PERSON_ID, 10);
        verify(messageConfig).getMessage(MessageKeyConstants.DRIVER_NO_RATES);
    }

    @Test
    void testGetAveragePassengerRating() {
        List<Rate> passengerRates = TestDataUtil.createPassengerRates();
        Double expectedAverage = 3.5;
        
        when(rateRepository.getLastRatesByPersonId(TestDataUtil.PASSENGER_PERSON_ID, 10))
                .thenReturn(passengerRates);
        
        AverageRatingResponse result = ratingService.getAveragePassengerRating(TestDataUtil.PASSENGER_ID);
        
        assertEquals(TestDataUtil.PASSENGER_PERSON_ID, result.personId());
        assertEquals(expectedAverage, result.averageRating());
        
        verify(rateRepository).getLastRatesByPersonId(TestDataUtil.PASSENGER_PERSON_ID, 10);
    }

    @Test
    void testGetAveragePassengerRating_NoRates() {
        when(rateRepository.getLastRatesByPersonId(TestDataUtil.PASSENGER_PERSON_ID, 10))
                .thenReturn(List.of());
        when(messageConfig.getMessage(MessageKeyConstants.PASSENGER_NO_RATES))
                .thenReturn(TestDataUtil.ERROR_PASSENGER_NO_RATES);
        
        assertThrows(NoRatesException.class,
                () -> ratingService.getAveragePassengerRating(TestDataUtil.PASSENGER_ID));
        
        verify(rateRepository).getLastRatesByPersonId(TestDataUtil.PASSENGER_PERSON_ID, 10);
        verify(messageConfig).getMessage(MessageKeyConstants.PASSENGER_NO_RATES);
    }

    @Test
    void testEmit() {
        String key = TestDataUtil.RIDE_ID;
        RidePersonRequest value = TestDataUtil.createDriverPersonRequest();
        
        when(emitter.send(kafkaRecordCaptor.capture())).thenReturn(null);
        
        ratingService.emit(key, value);
        
        verify(txSyncRegistry).registerInterposedSynchronization(synchronizationCaptor.capture());
        
        Synchronization synchronization = synchronizationCaptor.getValue();
        synchronization.beforeCompletion();
        synchronization.afterCompletion(Status.STATUS_COMMITTED);
        
        verify(emitter).send(kafkaRecordCaptor.capture());
    }
}
