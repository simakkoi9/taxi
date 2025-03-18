Feature: Rating API test

  Background:
    Given the database is clean

  Scenario: Successfully create a rating for a completed ride
    Given a completed ride with ID "qwe61287dgqwe712u"
    When I create a rating for the ride
    Then the rating is successfully created
    And I can retrieve the created rating

  Scenario: Attempt to create a duplicate rating
    Given an existing rating for ride with ID "qwe61287dgqwe712u"
    When I create a rating for the same ride
    Then I receive a duplicate rating error

  Scenario: Rate a driver for an existing rating
    Given an existing rating without driver rate
    When I set driver rate to "5" with comment "Great driver"
    Then the driver rate is successfully updated

  Scenario: Rate a passenger for an existing rating
    Given an existing rating without passenger rate
    When I set passenger rate to "4" with comment "Good passenger"
    Then the passenger rate is successfully updated

  Scenario: Attempt to set invalid driver rate
    Given an existing rating without driver rate
    When I set driver rate to "-1" with comment "Invalid rate"
    Then I receive a validation error for invalid rate

  Scenario: Get average driver rating
    Given multiple ratings exist for driver with ID "100"
    When I request the average rating for the driver
    Then I receive the driver's average rating "4.5"

  Scenario: Get average passenger rating
    Given multiple ratings exist for passenger with ID "200"
    When I request the average rating for the passenger
    Then I receive the passenger's average rating "3.5"

  Scenario: Attempt to rate an uncompleted ride
    Given an uncompleted ride with ID "ajcnbkjabck123123"
    When I create a rating for an uncompleted ride
    Then I receive an uncompleted ride error