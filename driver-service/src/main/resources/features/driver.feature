@order2
Feature: Driver API test

  Scenario: Create a new driver
    Given driver request body:
    """
    {
        "externalId": "default",
        "name": "name",
        "email": "email@mail.com",
        "phone": "+375442345678",
        "gender": "MALE"
    }
    """
    When sending request to create new driver
    Then should get a driver response with status 200

  Scenario: Create new driver, driver already exists
    Given driver request body:
    """
    {
        "externalId": "default",
        "name": "name",
        "email": "email@mail.com",
        "phone": "+375442345678",
        "gender": "MALE"
    }
    """
    When sending request to create new driver
    Then should get a driver error response with status 400

  Scenario: Get driver by id
    Given driver id: 1
    When sending request to get driver by id
    Then should get a driver response with status 200

  Scenario: Get driver by id, driver not found
    Given driver id: 999
    When sending request to get driver by id
    Then should get a driver error response with status 404

  Scenario: Update existing driver
    Given driver id: 1
    Given driver request body:
    """
    {
        "name": "otherName"
    }
    """
    When sending request to update driver
    Then should get a updated driver with status 200

  Scenario: Update driver, driver not found
    Given driver id: 999
    Given driver request body:
    """
    {
        "name": "otherName"
    }
    """
    When sending request to update driver
    Then should get a driver error response with status 404

  Scenario: Get all drivers
    When sending request to get all drivers
    Then should get a list of drivers with status 200

  Scenario: Set car for driver
    Given driver id: 1
    Given car id for driver: 1
    When sending request to set car for driver
    Then should get a driver with car response with status 200

  Scenario: Set car for driver, driver not found
    Given driver id: 999
    Given car id for driver: 1
    When sending request to set car for driver
    Then should get a driver error response with status 404

  Scenario: Set car for driver, car not found
    Given driver id: 1
    Given car id for driver: 999
    When sending request to set car for driver
    Then should get a driver error response with status 409

  Scenario: Remove car from driver
    Given driver id: 1
    When sending request to remove car from driver
    Then should get a driver response with status 200

  Scenario: Remove car from driver, driver not found
    Given driver id: 999
    When sending request to remove car from driver
    Then should get a driver error response with status 404

  Scenario: Delete driver
    Given driver id: 1
    When sending request to delete driver
    Then should get a driver response with status 200

  Scenario: Delete driver, driver not found
    Given driver id: 999
    When sending request to delete driver
    Then should get a driver error response with status 404