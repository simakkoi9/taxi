package io.simakkoi9.ridesservice.model.mapper;

import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.Ride;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RideMapper {
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "orderDateTime", expression = "java(LocalDateTime.now())")
    Ride toEntity(RideCreateRequest rideCreateRequest);

    RideResponse toResponse(Ride ride);

    List<RideResponse> toResponseList(List<Ride> rides);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(RideUpdateRequest rideUpdateRequest, @MappingTarget Ride ride);
}
