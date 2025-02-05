package io.simakkoi9.ratingservice.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.simakkoi9.ratingservice.model.entity.Rating;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RatingRepository implements PanacheRepository<Rating> {
    public boolean existsByRideId(String rideId) {
        return count("rideId = ?1", rideId) > 0;
    }

    public List<Rating> findAllRatingsByRideIdIn(List<Long> rideIdList) {
        return  find("rideId IN ?1", rideIdList)
                .list();
    }
}
