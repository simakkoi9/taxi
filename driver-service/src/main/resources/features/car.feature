@order1
Feature: Car API test

  Scenario: Create a new car
    Given car request body:
    """
    {
        "brand": "Toyota",
        "model": "Camry",
        "color": "White",
        "number": "BY1563"
    }
    """
    When sending request to create new car
    Then should get a car response with status 200

  Scenario: Create another car
    Given car request body:
    """
    {
        "brand": "Subaru",
        "model": "Impreza",
        "color": "Blue",
        "number": "BY1671"
    }
    """
    When sending request to create new car
    Then should get a car response with status 200

  Scenario: Create new car, car already exists
    Given car request body:
    """
    {
        "brand": "Toyota",
        "model": "Camry",
        "color": "White",
        "number": "BY1563"
    }
    """
    When sending request to create new car
    Then should get a car error response with status 400

  Scenario: Update existing car
    Given car id: 1
    Given car request body:
    """
    {
        "model": "Corolla"
    }
    """
    When sending request to update car
    Then should get a updated car with status 200

  Scenario: Update car, car not found
    Given car id: 999
    Given car request body:
    """
    {
        "model": "Corolla"
    }
    """
    When sending request to update car
    Then should get a car error response with status 404

  Scenario: Get car by id
    Given car id: 1
    When sending request to get car by id
    Then should get a car response with status 200

  Scenario: Get car by id, car not found
    Given car id: 999
    When sending request to get car by id
    Then should get a car error response with status 404

  Scenario: Delete car
    Given car id: 2
    When sending request to delete car
    Then should get a car response with status 200

  Scenario: Delete car, car not found
    Given car id: 999
    When sending request to delete car
    Then should get a car error response with status 404

  Scenario: Get all cars
    When sending request to get all cars
    Then should get a list of cars with status 200

