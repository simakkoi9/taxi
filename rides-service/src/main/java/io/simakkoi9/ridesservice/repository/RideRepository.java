package io.simakkoi9.ridesservice.repository;

import io.simakkoi9.ridesservice.model.entity.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends MongoRepository<Ride, Long> {

}
