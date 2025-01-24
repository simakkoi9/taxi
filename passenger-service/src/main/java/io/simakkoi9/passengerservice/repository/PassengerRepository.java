package io.simakkoi9.passengerservice.repository;

import io.simakkoi9.passengerservice.model.entity.Passenger;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends CrudRepository<Passenger, Long> {

    boolean existsByEmail(String email);
    Optional<Passenger> findPassengerByEmail(String email);
}
