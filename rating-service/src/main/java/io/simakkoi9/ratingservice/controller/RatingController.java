package io.simakkoi9.ratingservice.controller;

import io.simakkoi9.ratingservice.model.dto.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingPageResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingResponse;
import io.simakkoi9.ratingservice.service.RatingService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/ratings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RatingController {
    @Inject
    RatingService ratingService;

    @POST
    public RatingResponse createRating(@Valid RatingCreateRequest ratingCreateRequest) {
        return ratingService.createRating(ratingCreateRequest);
    }

    @GET
    public RatingPageResponse getAllRatings(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return ratingService.getAllRatings(page, size);
    }

    @GET
    @Path("/{id}")
    public RatingResponse getRating(@PathParam("id") Long id) {
        return ratingService.getRating(id);
    }

    @PATCH
    @Path("/{id}/driver/rate")
    public RatingResponse setRateForDriver(
            @Valid DriverRatingUpdateRequest driverRatingUpdateRequest,
            @PathParam("id") Long id
    ) {
        return ratingService.setRateForDriver(id, driverRatingUpdateRequest);
    }

    @PATCH
    @Path("/{id}/passenger/rate")
    public RatingResponse setRateForPassenger(
            @Valid PassengerRatingUpdateRequest passengerRatingUpdateRequest,
            @PathParam("id") Long id
    ) {
        return ratingService.setRateForPassenger(id, passengerRatingUpdateRequest);
    }

    @GET
    @Path("/driver/{id}")
    public AverageRatingResponse getAverageDriverRating(
            @PathParam("id") Long id
    ) {
        return ratingService.getAverageDriverRating(id);
    }

    @GET
    @Path("/passenger/{id}")
    public AverageRatingResponse getAveragePassengerRating(
            @PathParam("id") Long id
    ) {
        return ratingService.getAveragePassengerRating(id);
    }

}
