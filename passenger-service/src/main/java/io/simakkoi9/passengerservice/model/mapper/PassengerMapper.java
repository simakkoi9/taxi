package io.simakkoi9.passengerservice.model.mapper;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;
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
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PassengerMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Passenger toEntity(PassengerCreateRequest passengerCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void setPassengerUpdateRequest(PassengerUpdateRequest passengerUpdateRequest, @MappingTarget Passenger passenger);

    PassengerResponse toResponse(Passenger passenger);

    List<PassengerResponse> toResponseList(List<Passenger> passengerList);

    default PageResponse<PassengerResponse> toPageResponse(Page<Passenger> passengers) {
        List<PassengerResponse> passengerResponseList = toResponseList(passengers.getContent());
        return new PageResponse<>(
                passengerResponseList,
                passengers.getSize(),
                passengers.getNumber(),
                passengers.getTotalPages(),
                passengers.getTotalElements()
        );
    }

}
