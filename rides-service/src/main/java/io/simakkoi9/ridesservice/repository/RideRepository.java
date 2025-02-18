package io.simakkoi9.ridesservice.repository;

import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
    boolean existsByPassenger_IdAndStatusIn(Long passengerId, List<RideStatus> statusList);

    List<Ride> findAllByStatusIn(List<RideStatus> statusList);
}
