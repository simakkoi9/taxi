package io.simakkoi9.ratingservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.simakkoi9.ratingservice.model.dto.rest.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingPageResponse;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingResponse;
import io.simakkoi9.ratingservice.model.entity.Rating;
import io.simakkoi9.ratingservice.service.RatingService;
import io.simakkoi9.ratingservice.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

@QuarkusTest
class RatingControllerTest {
    @InjectMock
    RatingService ratingService;

    private RatingController ratingController;

    @BeforeEach
    void setUp() {
        ratingController = new RatingController();
        ratingController.ratingService = this.ratingService;
    }

    @Test
    void testCreateRating() {
        RatingCreateRequest request = TestDataUtil.createRatingRequest();
        Rating rating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(rating);
        
        when(ratingService.createRating(request)).thenReturn(expectedResponse);
        
        RatingResponse result = ratingController.createRating(request);
        
        assertEquals(expectedResponse, result);
        verify(ratingService).createRating(request);
    }

    @Test
    void testGetAllRatings() {
        int page = TestDataUtil.PAGE;
        int size = TestDataUtil.SIZE;
        Rating rating = TestDataUtil.createRating();
        RatingResponse ratingResponse = TestDataUtil.createRatingResponse(rating);
        RatingPageResponse expectedResponse = RatingPageResponse.builder()
                .content(List.of(ratingResponse))
                .currentPage(page)
                .size(size)
                .totalPages(TestDataUtil.TOTAL_PAGES)
                .build();
        
        when(ratingService.getAllRatings(page, size)).thenReturn(expectedResponse);
        
        RatingPageResponse result = ratingController.getAllRatings(page, size);
        
        assertEquals(expectedResponse, result);
        verify(ratingService).getAllRatings(page, size);
    }

    @Test
    void testGetRating() {
        Long id = TestDataUtil.RATING_ID;
        Rating rating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(rating);
        
        when(ratingService.getRating(id)).thenReturn(expectedResponse);
        
        RatingResponse result = ratingController.getRating(id);
        
        assertEquals(expectedResponse, result);
        verify(ratingService).getRating(id);
    }

    @Test
    void testSetRateForDriver() {
        Long id = TestDataUtil.RATING_ID;
        DriverRatingUpdateRequest request = TestDataUtil.createDriverRatingUpdateRequest();
        Rating rating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(rating);
        
        when(ratingService.setRateForDriver(id, request)).thenReturn(expectedResponse);
        
        RatingResponse result = ratingController.setRateForDriver(request, id);
        
        assertEquals(expectedResponse, result);
        verify(ratingService).setRateForDriver(id, request);
    }

    @Test
    void testSetRateForPassenger() {
        Long id = TestDataUtil.RATING_ID;
        PassengerRatingUpdateRequest request = TestDataUtil.createPassengerRatingUpdateRequest();
        Rating rating = TestDataUtil.createRating();
        RatingResponse expectedResponse = TestDataUtil.createRatingResponse(rating);
        
        when(ratingService.setRateForPassenger(id, request)).thenReturn(expectedResponse);
        
        RatingResponse result = ratingController.setRateForPassenger(request, id);
        
        assertEquals(expectedResponse, result);
        verify(ratingService).setRateForPassenger(id, request);
    }

    @Test
    void testGetAverageDriverRating() {
        Long id = TestDataUtil.DRIVER_ID;
        AverageRatingResponse expectedResponse = TestDataUtil.createDriverAverageRatingResponse();
        
        when(ratingService.getAverageDriverRating(id)).thenReturn(expectedResponse);
        
        AverageRatingResponse result = ratingController.getAverageDriverRating(id);
        
        assertEquals(expectedResponse, result);
        verify(ratingService).getAverageDriverRating(id);
    }

    @Test
    void testGetAveragePassengerRating() {
        Long id = TestDataUtil.PASSENGER_ID;
        AverageRatingResponse expectedResponse = TestDataUtil.createPassengerAverageRatingResponse();
        
        when(ratingService.getAveragePassengerRating(id)).thenReturn(expectedResponse);
        
        AverageRatingResponse result = ratingController.getAveragePassengerRating(id);
        
        assertEquals(expectedResponse, result);
        verify(ratingService).getAveragePassengerRating(id);
    }
}
