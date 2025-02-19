package io.simakkoi9.ridesservice.model.mapper;

import io.simakkoi9.ridesservice.model.dto.feign.PassengerRequest;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-20T01:06:21+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class PassengerMapperImpl implements PassengerMapper {

    @Override
    public Passenger toEntity(PassengerRequest passengerRequest) {
        if ( passengerRequest == null ) {
            return null;
        }

        Passenger passenger = new Passenger();

        passenger.setId( passengerRequest.id() );
        passenger.setName( passengerRequest.name() );
        passenger.setEmail( passengerRequest.email() );
        passenger.setPhone( passengerRequest.phone() );

        return passenger;
    }
}
