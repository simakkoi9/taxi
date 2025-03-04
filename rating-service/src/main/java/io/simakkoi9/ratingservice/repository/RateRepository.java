package io.simakkoi9.ratingservice.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.simakkoi9.ratingservice.model.entity.Rate;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RateRepository implements PanacheRepository<Rate> {

    public List<Rate> getLastRatesByPersonId(String personId, int limit) {
        return find("personId = ?1", Sort.descending("id"), personId)
                .page(Page.of(0, limit))
                .list();

    }

}
