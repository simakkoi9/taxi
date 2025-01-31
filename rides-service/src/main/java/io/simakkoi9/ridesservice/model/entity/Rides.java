package io.simakkoi9.ridesservice.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "rides")
public class Rides {
    @Id
    private Long id;

    private Driver driver;

    private Passenger passenger;

    private String pickupAddress;

    private String destinationAddress;

    private BigDecimal cost;

    private RideStatus status;

    private LocalDateTime orderDatetime;
}
