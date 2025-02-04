package io.simakkoi9.ratingservice.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.simakkoi9.ratingservice.model.entity.Rating;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RatingRepository implements PanacheRepository<Rating> {

}
