package io.simakkoi9.ridesservice.model.mapper;

import io.simakkoi9.ridesservice.model.dto.kafka.CarRequest;
import io.simakkoi9.ridesservice.model.dto.kafka.DriverRequest;
import io.simakkoi9.ridesservice.model.entity.Car;
import io.simakkoi9.ridesservice.model.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {
    Driver toEntity(DriverRequest request);

    DriverRequest toRequest(Driver driver);

    Car toEntity(CarRequest request);

    CarRequest toRequest(Car car);
}
