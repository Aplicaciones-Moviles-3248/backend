Feature: Notifications Management

  Scenario: Retrieve my notifications successfully
    Given the notification service is available
    And the user is authenticated for notifications
    When the user retrieves my notifications
    Then the notification response status should be 200

  Scenario: Retrieve unread notifications count successfully
    Given the notification service is available
    And the user is authenticated for notifications
    When the user retrieves my unread notifications count
    Then the notification response status should be 200