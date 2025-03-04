package io.simakkoi9.ridesservice.model.mapper;

import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.rest.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.Ride;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RideMapper {
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "orderDateTime", expression = "java(LocalDateTime.now())")
    Ride toEntity(RideCreateRequest rideCreateRequest);

    RideResponse toResponse(Ride ride);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(RideUpdateRequest rideUpdateRequest, @MappingTarget Ride ride);

    List<RideResponse> toResponseList(List<Ride> rides);

    default PageResponse<RideResponse> toPageResponse(Page<Ride> rides) {
        List<RideResponse> rideResponseList = toResponseList(rides.getContent());
        return new PageResponse<>(
                rideResponseList,
                rides.getSize(),
                rides.getNumber(),
                rides.getTotalPages(),
                rides.getTotalElements()
        );
    }
}
