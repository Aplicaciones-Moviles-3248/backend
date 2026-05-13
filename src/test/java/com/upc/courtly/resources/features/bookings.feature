Feature: Bookings Management

  Scenario: Create a new booking successfully
    Given the user is authenticated
    When the user creates a booking
    Then the booking response status should be 201

  Scenario: Retrieve my bookings
    Given the user is authenticated
    When the user retrieves bookings
    Then the booking response status should be 200

  Scenario: Cancel a booking successfully
    Given the user is authenticated
    And the user creates a booking
    When the user cancels the booking
    Then the booking response status should be 200