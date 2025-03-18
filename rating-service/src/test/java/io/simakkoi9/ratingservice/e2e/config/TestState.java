package io.simakkoi9.ratingservice.e2e.config;

import io.simakkoi9.ratingservice.repository.RateRepository;
import io.simakkoi9.ratingservice.repository.RatingRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.quarkiverse.cucumber.ScenarioScope;

@ScenarioScope
public class TestState {

    @Inject
    RatingRepository ratingRepository;

    @Inject
    RateRepository rateRepository;

    @Inject
    EntityManager em;

    private Long currentRatingId;
    private String currentRideId;
    private String currentDriverId;

    @Before
    @Transactional
    public void setUp(Scenario scenario) throws Exception {
        cleanDatabase();
        resetState();
    }

    @After
    @Transactional
    public void tearDown(Scenario scenario) throws Exception {
        cleanDatabase();
    }

    @Transactional
    public void cleanDatabase() {
        em.createNativeQuery("SET CONSTRAINTS ALL DEFERRED").executeUpdate();

        rateRepository.deleteAll();
        ratingRepository.deleteAll();

        em.createNativeQuery("ALTER SEQUENCE rating_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE rates_id_seq RESTART WITH 1").executeUpdate();

        em.createNativeQuery("SET CONSTRAINTS ALL IMMEDIATE").executeUpdate();

        em.flush();
        em.clear();
    }

    private void resetState() {
        currentRatingId = null;
        currentRideId = null;
        currentDriverId = null;
    }

    public Long getCurrentRatingId() {
        return currentRatingId;
    }

    public void setCurrentRatingId(Long ratingId) {
        this.currentRatingId = ratingId;
    }

    public String getCurrentRideId() {
        return currentRideId;
    }

    public void setCurrentRideId(String rideId) {
        this.currentRideId = rideId;
    }

    public String getCurrentDriverId() {
        return currentDriverId;
    }

    public void setCurrentDriverId(String driverId) {
        this.currentDriverId = driverId;
    }
    
}