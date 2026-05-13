package com.upc.courtly.steps;

import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CourtsSteps {

    private Response response;

    private final String BASE_URL = "http://localhost:8080/api/v1/courts";

    private final String TOKEN = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbjEiLCJpYXQiOjE3Nzg2NDk1MzksImV4cCI6MTc3OTI1NDMzOX0.9s8SOf1_yhpEu7zebDYoWquAyLevupj7Zodw8leCouUOAqvX5SaD0xzM0-SoM5hy";

    @Given("the court service is available")
    public void theCourtServiceIsAvailable() {
        assert BASE_URL != null;
    }

    @When("the client creates a court with name {string}")
    public void theClientCreatesACourt(String name) {

        String body = String.format("""
            {
              "name": "%s",
              "location": "Lima",
              "type": "Indoor",
              "imageUrl": "https://image.com/court.jpg",
              "pricePerHour": 50
            }
            """, name);

        response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", TOKEN)
                .body(body)
                .when()
                .post(BASE_URL);

        response.prettyPrint();
    }

    @When("the client requests all courts")
    public void theClientRequestsAllCourts() {

        response = given()
                .header("Authorization", TOKEN)
                .when()
                .get(BASE_URL);

        response.prettyPrint();
    }

    @When("the client requests the court with id {int}")
    public void theClientRequestsCourtById(int id) {

        response = given()
                .header("Authorization", TOKEN)
                .when()
                .get(BASE_URL + "/" + id);

        response.prettyPrint();
    }

    @Then("the court response status should be {int}")
    public void theCourtResponseStatusShouldBe(int statusCode) {

        assertThat(response.getStatusCode(), equalTo(statusCode));
    }

    @Then("the response should contain court name {string}")
    public void theResponseShouldContainCourtName(String expectedName) {

        String actualName = response.jsonPath().getString("name");

        assertThat(actualName, equalTo(expectedName));
    }
}