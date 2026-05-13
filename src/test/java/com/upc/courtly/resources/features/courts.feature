Feature: Court Management

  Scenario: Create a new court successfully
    Given the court service is available
    When the client creates a court with name "Central Court"
    Then the court response status should be 201
    And the response should contain court name "Central Court"

  Scenario: Retrieve all courts
    Given the court service is available
    When the client requests all courts
    Then the court response status should be 200

  Scenario: Retrieve a court by id
    Given the court service is available
    When the client requests the court with id 1
    Then the court response status should be 200