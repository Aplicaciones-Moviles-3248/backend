Feature: User Authentication

  Scenario: Successfully sign up with valid credentials
    Given the authentication service is available
    When the client sends a sign-up request with username "Kevin Herrera" and password "123456789" and roles "ROLE_USER"
    Then the response status should be 201
    And the response should contain username "Kevin Herrera"

  Scenario: Successfully sign in with valid credentials
    Given the authentication service is available
    When the client sends a sign-in request with username "Kevin Herrera" and password "123456789"
    Then the response status should be 200
    And the authentication response should contain a token

  Scenario: Fail sign in with invalid password
    Given the authentication service is available
    When the client sends a sign-in request with username "Kevin Herrera" and password "12345678"
    Then the response status should be 401