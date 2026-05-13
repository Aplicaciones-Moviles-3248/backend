package com.upc.courtly.steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BookingsSteps {

    private static final String BASE_URL = "http://localhost:8080/api/v1";

    private String token;
    private Long bookingId;
    private Response response;

    @Given("the user is authenticated")
    public void theUserIsAuthenticated() {

        RestAssured.baseURI = BASE_URL;

        String username = "test_" + UUID.randomUUID();
        String password = "123456";

        Map<String, Object> registerBody = new HashMap<>();
        registerBody.put("username", username);
        registerBody.put("password", password);

        Response registerResponse =
                given()
                        .contentType("application/json")
                        .body(registerBody)
                        .when()
                        .post("/authentication/sign-up");

        System.out.println("SIGN UP STATUS: " + registerResponse.getStatusCode());
        System.out.println(registerResponse.getBody().asString());

        assertThat(registerResponse.getStatusCode(), equalTo(201));

        Response loginResponse =
                given()
                        .contentType("application/json")
                        .body(registerBody)
                        .when()
                        .post("/authentication/sign-in");

        System.out.println("LOGIN STATUS: " + loginResponse.getStatusCode());
        System.out.println(loginResponse.getBody().asString());

        assertThat(loginResponse.getStatusCode(), equalTo(200));

        token = loginResponse.jsonPath().getString("token");

        System.out.println("TOKEN: " + token);

        assertThat(token, notNullValue());
    }

    @When("the user creates a booking")
    public void theUserCreatesABooking() {

        Map<String, Object> bookingBody = new HashMap<>();
        bookingBody.put("courtId", 1);
        bookingBody.put("date", "2026-06-01");
        bookingBody.put("startTime", "10:00");
        bookingBody.put("endTime", "11:00");

        response =
                given()
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .body(bookingBody)
                        .when()
                        .post("/bookings");

        System.out.println("CREATE BOOKING STATUS: " + response.getStatusCode());
        System.out.println(response.getBody().asString());

        if (response.getStatusCode() == 201) {
            bookingId = response.jsonPath().getLong("id");
        }
    }

    @When("the user retrieves bookings")
    public void theUserRetrievesBookings() {

        response =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/bookings");

        System.out.println("GET BOOKINGS STATUS: " + response.getStatusCode());
        System.out.println(response.getBody().asString());
    }

    @When("the user cancels the booking")
    public void theUserCancelsTheBooking() {

        response =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .put("/bookings/" + bookingId + "/cancel");

        System.out.println("CANCEL BOOKING STATUS: " + response.getStatusCode());
        System.out.println(response.getBody().asString());
    }

    @Then("the booking response status should be {int}")
    public void theBookingResponseStatusShouldBe(Integer expectedStatus) {

        assertThat(response.getStatusCode(), equalTo(expectedStatus));
    }
}