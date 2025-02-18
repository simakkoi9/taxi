package io.simakkoi9.passengerservice.repository;

import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends CrudRepository<Passenger, Long> {
    boolean existsByEmailAndStatus(String email, UserStatus status);

    Optional<Passenger> findByIdAndStatus(Long id, UserStatus status);

    Page<Passenger> findAllByStatus(UserStatus status, Pageable pageable);

}
