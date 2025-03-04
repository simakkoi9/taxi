package io.simakkoi9.ratingservice.model.mapper;

import io.simakkoi9.ratingservice.model.dto.rest.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingResponse;
import io.simakkoi9.ratingservice.model.entity.Rating;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI
)
public interface RatingMapper {
    Rating toEntity(RatingCreateRequest ratingCreateRequest);

    RatingResponse toResponse(Rating rating);

    List<RatingResponse> toResponseList(List<Rating> ratingList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Rating driverRatingPartialUpdate(DriverRatingUpdateRequest driverRatingUpdateRequest, @MappingTarget Rating rating);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Rating passengerRatingPartialUpdate(
            PassengerRatingUpdateRequest passengerRatingUpdateRequest,
            @MappingTarget Rating rating
    );
}