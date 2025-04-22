Feature: Passenger API test

  Scenario: Create a new passenger successfully
    When I create a new passenger with details:
      | externalId | name  | email           | phone         |
      | default    | name  | email@mail.com  | +375293453434 |
    Then Passenger should be created successfully
    And Passenger details should match:
      | name  | email           | phone         |
      | name  | email@mail.com  | +375293453434 |

  Scenario: Create a passenger with duplicate email
    Given a passenger exists in the system
    When I create another passenger with the same email
    Then I should receive a 409 error with a duplicate passenger message

  Scenario: Create a passenger with invalid details
    When I create a passenger with invalid details
    Then the system should return validation error
    And the response should contain 3 errors

  Scenario: Get an existing passenger by ID
    Given a passenger exists in the system
    When I request the passenger details by ID
    Then I should receive the passenger's information

  Scenario: Get a non-existent passenger
    Given no passenger exists with ID 999
    When I request the passenger details by ID
    Then I should receive a 404 error

  Scenario: Update an existing passenger
    Given a passenger exists in the system
    When I update the passenger with details:
      | name      | email               | phone         |
      | otherName | otherEmail@mail.com | +375298765432 |
    Then the passenger's details should be updated successfully
    And Passenger details should match:
      | name      | email               | phone         |
      | otherName | otherEmail@mail.com | +375298765432 |

  Scenario: Delete a passenger
    Given a passenger exists in the system
    When I delete the passenger by ID
    Then the passenger's status should be set to "DELETED"

  Scenario: Get all passengers
    Given a passenger exists in the system
    When I request all passengers
    Then the response should contain passengers

  Scenario: Get passengers with invalid pagination
    Given a passenger exists in the system
    When I request passengers with invalid pagination
    Then the system should return validation error
    And the response should contain pagination errors
