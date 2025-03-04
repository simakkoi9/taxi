package io.simakkoi9.ridesservice.model.mapper;

import io.simakkoi9.ridesservice.model.dto.kafka.KafkaCarDto;
import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverRequest;
import io.simakkoi9.ridesservice.model.entity.Car;
import io.simakkoi9.ridesservice.model.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

    Driver toEntity(KafkaDriverRequest kafkaDriverRequest);

    Car toEntity(KafkaCarDto kafkaCarDto);

}
