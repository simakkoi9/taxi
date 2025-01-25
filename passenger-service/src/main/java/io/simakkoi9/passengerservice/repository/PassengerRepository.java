package io.simakkoi9.passengerservice.repository;

import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends CrudRepository<Passenger, Long> {
    boolean existsByEmailAndStatus(String email, UserStatus status);

    Optional<Passenger> findByIdAndStatus(Long id, UserStatus status);

    Iterable<Passenger> findAllByStatus(UserStatus status);

}
