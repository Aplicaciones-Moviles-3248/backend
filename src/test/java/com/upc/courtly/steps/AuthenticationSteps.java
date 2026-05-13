package com.upc.courtly.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AuthenticationSteps {

    private Response response;

    private static final String BASE_URL =
            "http://localhost:8080/api/v1/authentication";

    @Given("the authentication service is available")
    public void theAuthenticationServiceIsAvailable() {
        assertThat(BASE_URL, notNullValue());
    }

    @When("the client sends a sign-up request with username {string} and password {string} and roles {string}")
    public void signUpRequest(String username, String password, String rolesCsv) {

        List<String> roles = List.of(rolesCsv.split(","));

        String rolesJson = roles.stream()
                .map(role -> "\"" + role.trim() + "\"")
                .toList()
                .toString();

        String body = String.format("""
                {
                  "username": "%s",
                  "password": "%s",
                  "roles": %s
                }
                """, username, password, rolesJson);

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(BASE_URL + "/sign-up");
    }

    @When("the client sends a sign-in request with username {string} and password {string}")
    public void signInRequest(String username, String password) {

        String body = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """, username, password);

        response = given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(BASE_URL + "/sign-in");
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int statusCode) {
        assertThat(response.getStatusCode(), equalTo(statusCode));
    }

    @And("the response should contain username {string}")
    public void responseShouldContainUsername(String username) {

        String actualUsername = response.jsonPath().getString("username");

        assertThat(actualUsername, equalTo(username));
    }

    @And("the authentication response should contain a token")
    public void authenticationResponseShouldContainToken() {

        String token = response.jsonPath().getString("token");

        assertThat(token, notNullValue());
        assertThat(token.length(), greaterThan(10));
    }
}