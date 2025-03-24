Feature: Ride Service E2E Tests

  Background:
    Given a passenger exists in the system
    And route service is active

  Scenario: Successfully create and complete a ride
    When I create a new ride
    Then the ride should be created successfully
    And the ride status should be "CREATED"
    And the ride cost should be 6.75
    
    When I request a driver for the ride
    Then a driver should be assigned
    And the ride status should be "ACCEPTED"
    
    When I update the ride status to "EN_ROUTE_TO_PASSENGER"
    Then the ride status should be "EN_ROUTE_TO_PASSENGER"
    When I update the ride status to "EN_ROUTE_TO_DESTINATION"
    Then the ride status should be "EN_ROUTE_TO_DESTINATION"
    When I update the ride status to "COMPLETED"
    Then the ride status should be "COMPLETED"

  Scenario: Update ride addresses
    Given I have created a ride
    When I update ride addresses
    Then the ride addresses should be updated
    And the ride cost should be 6.75

  Scenario: Invalid ride creation
    When I create a ride with invalid coordinates
    Then the system should return validation error
    And the response should contain 2 errors

  Scenario: Create ride for busy passenger
    Given a passenger has an active ride
    When I create a new ride
    Then the system should return conflict error
    And the response should contain 1 errors

  Scenario: Create ride for non-existent passenger
    When I create a ride with invalid passenger
    Then the system should return not found error
    And the response should contain 1 errors

  Scenario: No available drivers
    Given I have created a ride
    When I request an unavailable driver for the ride
    Then the system should return no available drivers error
    And the ride status should remain "CREATED"

  Scenario: Invalid status transition
    Given I have created a ride
    When I try to update status from "CREATED" to "EN_ROUTE_TO_DESTINATION"
    Then the system should return invalid status transition error
    And the ride status should remain "CREATED"

  Scenario: Get rides with pagination
    Given two rides exist in the system
    When I request rides with invalid pagination
    Then the system should return validation error
    And the response should contain 2 errors

  Scenario: Successfully get paginated rides
    Given two rides exist in the system
    When I request rides with valid pagination
    Then the response should contain pagination information
    And the response should contain 2 rides in content