package io.simakkoi9.ratingservice.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "rating")
public class Rating extends PanacheEntity {
    private String rideId;

    private Integer rateForDriver;

    private Integer rateForPassenger;

    private String commentForDriver;

    private String commentForPassenger;
}
