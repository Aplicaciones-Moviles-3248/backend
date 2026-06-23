package com.upc.courtly.iam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Integration test that exercises the real Spring Security filter chain end to end:
 * sign-up -> sign-in -> authenticated requests with the issued Bearer token.
 *
 * It guards against the regression where the backend rejects its own freshly issued
 * JWT with HTTP 401 on every authenticated endpoint. A coherent build MUST accept the
 * token it just signed (the signing key and the validation key are the same property),
 * so this test pins that contract.
 *
 * Runs against an in-memory H2 database under the "local" profile so the role seeder
 * (disabled under the "test" profile) runs and the sign-up flow succeeds.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:courtly_it;DB_CLOSE_DELAY=-1;MODE=LEGACY",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "authorization.jwt.secret=IntegrationTestSecretKeyForJwtTokens1234567890",
        "authorization.jwt.expiration.days=7"
})
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AuthenticationFlowIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void issuedTokenIsAcceptedByProtectedEndpoints() throws Exception {
        var username = "it_user";
        var password = "123456";

        // Sign-up
        var signUpBody = "{\"username\":\"" + username + "\",\"password\":\"" + password
                + "\",\"roles\":[\"ROLE_USER\"]}";
        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpBody))
                .andExpect(result -> assertTrue(result.getResponse().getStatus() < 400,
                        "sign-up should succeed, got " + result.getResponse().getStatus()));

        // Sign-in
        var signInBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        MvcResult signInResult = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInBody))
                .andReturn();

        assertNotEquals(401, signInResult.getResponse().getStatus(), "sign-in must not be 401");
        JsonNode signInJson = objectMapper.readTree(signInResult.getResponse().getContentAsString());
        var token = signInJson.get("token").asText();
        var userId = signInJson.get("id").asLong();
        assertTrue(token != null && !token.isBlank(), "token must be present");

        var bearer = "Bearer " + token;

        // Authenticated write: the issued token MUST be accepted by the bearer filter.
        // 401 here is the regression: the backend rejecting its own JWT.
        var profileBody = "{\"name\":\"IT User\",\"email\":\"it_user@test.com\","
                + "\"phone\":\"999999999\",\"imageUrl\":\"https://example.com/a.png\",\"userId\":" + userId + "}";
        MvcResult createProfile = mockMvc.perform(post("/api/v1/user-profiles")
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(profileBody))
                .andReturn();

        assertNotEquals(401, createProfile.getResponse().getStatus(),
                "Authenticated endpoint rejected a freshly issued token (401). "
                        + "The backend does not accept its own JWT.");
        assertTrue(createProfile.getResponse().getStatus() < 400,
                "profile creation should succeed, got " + createProfile.getResponse().getStatus());

        // Authenticated read: the user's payments (empty list for a new user).
        MvcResult payments = mockMvc.perform(get("/api/v1/payments")
                        .header("Authorization", bearer))
                .andReturn();

        assertNotEquals(401, payments.getResponse().getStatus(),
                "GET /payments rejected a freshly issued token (401).");
        assertTrue(payments.getResponse().getStatus() < 400,
                "GET /payments should succeed, got " + payments.getResponse().getStatus());
    }
}
