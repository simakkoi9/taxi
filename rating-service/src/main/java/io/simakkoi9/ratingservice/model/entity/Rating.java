package io.simakkoi9.ratingservice.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Rating extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ride_id")
    private String rideId;

    @Column(name = "rate_for_driver")
    private Integer rateForDriver;

    @Column(name = "rate_for_passenger")
    private Integer rateForPassenger;

    @Column(name = "comment_for_driver")
    private String commentForDriver;

    @Column(name = "comment_for_passenger")
    private String commentForPassenger;

}
