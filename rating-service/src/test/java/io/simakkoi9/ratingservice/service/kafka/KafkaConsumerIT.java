package io.simakkoi9.ratingservice.service.kafka;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.simakkoi9.ratingservice.config.TestContainersConfig;
import io.simakkoi9.ratingservice.repository.RateRepository;
import io.simakkoi9.ratingservice.util.TestDataUtil;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionManager;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.smallrye.reactive.messaging.kafka.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestContainersConfig.class)
class KafkaConsumerIT {

    @Inject
    KafkaConsumer kafkaConsumer;
    
    @Inject
    RateRepository rateRepository;
    
    @Inject
    TransactionManager tm;
    
    @BeforeEach
    void setUp() throws Exception {
        tm.begin();
        rateRepository.deleteAll();
        tm.commit();
    }
    
    @Test
    void testListen_ShouldSaveDriverRate() throws Exception {
        var result = kafkaConsumer.listen(Record.of(
            TestDataUtil.KAFKA_DRIVER_PERSON_ID, 
            TestDataUtil.KAFKA_DRIVER_RATE))
            .subscribe().withSubscriber(UniAssertSubscriber.create());
            
        result.assertCompleted();
        
        tm.begin();
        var savedRate = rateRepository.find("personId", TestDataUtil.KAFKA_DRIVER_PERSON_ID).firstResult();
        tm.commit();

        assertAll(() -> {
            assertNotNull(savedRate);
            assertEquals(Integer.valueOf(TestDataUtil.KAFKA_DRIVER_RATE), savedRate.getRate());
            assertEquals(TestDataUtil.KAFKA_DRIVER_PERSON_ID, savedRate.getPersonId());
            assertTrue(savedRate.getPersonId().startsWith(TestDataUtil.DRIVER_PREFIX));
        });
    }
    
    @Test
    void testListen_ShouldSavePassengerRate() throws Exception {
        var result = kafkaConsumer.listen(Record.of(
            TestDataUtil.KAFKA_PASSENGER_PERSON_ID, 
            TestDataUtil.KAFKA_PASSENGER_RATE))
            .subscribe().withSubscriber(UniAssertSubscriber.create());
            
        result.assertCompleted();
        
        tm.begin();
        var savedRate = rateRepository.find("personId", TestDataUtil.KAFKA_PASSENGER_PERSON_ID).firstResult();
        tm.commit();

        assertAll(() -> {
            assertNotNull(savedRate);
            assertEquals(Integer.valueOf(TestDataUtil.KAFKA_PASSENGER_RATE), savedRate.getRate());
            assertEquals(TestDataUtil.KAFKA_PASSENGER_PERSON_ID, savedRate.getPersonId());
            assertTrue(savedRate.getPersonId().startsWith(TestDataUtil.PASSENGER_PREFIX));
        });
    }
    
    @Test
    void testListen_ShouldHandleMultipleMessages() throws Exception {
        var result1 = kafkaConsumer.listen(Record.of(
            TestDataUtil.KAFKA_DRIVER_PERSON_ID, 
            TestDataUtil.KAFKA_DRIVER_RATE))
            .subscribe().withSubscriber(UniAssertSubscriber.create());
            
        var result2 = kafkaConsumer.listen(Record.of(
            TestDataUtil.KAFKA_PASSENGER_PERSON_ID, 
            TestDataUtil.KAFKA_PASSENGER_RATE))
            .subscribe().withSubscriber(UniAssertSubscriber.create());
            
        result1.assertCompleted();
        result2.assertCompleted();
        
        tm.begin();
        var rates = rateRepository.listAll();
        tm.commit();
        
        assertEquals(2, rates.size());
        
        var savedDriverRate = rates.stream()
            .filter(r -> r.getPersonId().equals(TestDataUtil.KAFKA_DRIVER_PERSON_ID))
            .findFirst()
            .orElse(null);
            
        var savedPassengerRate = rates.stream()
            .filter(r -> r.getPersonId().equals(TestDataUtil.KAFKA_PASSENGER_PERSON_ID))
            .findFirst()
            .orElse(null);

        assertAll(() -> {
            assertNotNull(savedDriverRate);
            assertNotNull(savedPassengerRate);
            assertEquals(Integer.valueOf(TestDataUtil.KAFKA_DRIVER_RATE), savedDriverRate.getRate());
            assertEquals(Integer.valueOf(TestDataUtil.KAFKA_PASSENGER_RATE), savedPassengerRate.getRate());
        });
    }
} 