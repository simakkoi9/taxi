package io.simakkoi9.ridesservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {
    @Id
    private Long id;

    private Passenger passenger;

    private Driver driver;

    private String pickupAddress;

    private String destinationAddress;

    private BigDecimal cost;

    private RideStatus status;

    private LocalDateTime orderDatetime;
}
