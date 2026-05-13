package com.upc.courtly.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class NotificationsSteps {

    private static final String BASE_URL = "http://localhost:8080/api/v1/notifications";
    private static final String AUTH_URL = "http://localhost:8080/api/v1/authentication/sign-in";

    private Response response;
    private String token;

    @Given("the notification service is available")
    public void theNotificationServiceIsAvailable() {
        assertThat(BASE_URL, notNullValue());
    }

    @And("the user is authenticated for notifications")
    public void theUserIsAuthenticatedForNotifications() {

        String loginBody = """
            {
              "username": "admin1",
              "password": "123"
            }
            """;

        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post(AUTH_URL);

        System.out.println("LOGIN STATUS: " + loginResponse.getStatusCode());
        System.out.println(loginResponse.asString());

        assertThat(loginResponse.getStatusCode(), equalTo(200));

        token = "Bearer " + loginResponse.path("token").toString();

        assertThat(token, notNullValue());
    }

    @When("the user retrieves my notifications")
    public void theUserRetrievesMyNotifications() {
        response = given()
                .header("Authorization", token)
                .when()
                .get(BASE_URL + "/me");

        System.out.println("NOTIFICATIONS RESPONSE:");
        System.out.println(response.asString());
    }

    @When("the user retrieves my unread notifications count")
    public void theUserRetrievesMyUnreadNotificationsCount() {
        response = given()
                .header("Authorization", token)
                .when()
                .get(BASE_URL + "/me/unread-count");

        System.out.println("UNREAD COUNT RESPONSE:");
        System.out.println(response.asString());
    }

    @Then("the notification response status should be {int}")
    public void theNotificationResponseStatusShouldBe(int expectedStatus) {
        assertThat(response.getStatusCode(), equalTo(expectedStatus));
    }
}