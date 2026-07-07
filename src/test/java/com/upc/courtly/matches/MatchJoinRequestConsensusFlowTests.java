package com.upc.courtly.matches;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.courtly.courts.domain.model.aggregates.Court;
import com.upc.courtly.courts.infrastructure.persistence.jpa.repositories.CourtRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * End-to-end coverage for the real (backend-driven) consensus join-request flow:
 * a match with two existing participants requires BOTH to approve a third user's
 * join request before that user is actually added, and each step must produce a
 * real notification (SOC-03: no more client-only simulated consensus).
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:courtly_join_requests_it;DB_CLOSE_DELAY=-1;MODE=LEGACY",
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
class MatchJoinRequestConsensusFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourtRepository courtRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private record AuthedPlayer(String bearer, Long profileId) {}

    @Test
    void joinRequestOnlyResolvesOnceAllParticipantsApprove() throws Exception {
        var creator = registerPlayer("creator_it", "Creator One");
        var buddy = registerPlayer("buddy_it", "Buddy Two");
        var requester = registerPlayer("requester_it", "Requester Three");

        var court = courtRepository.save(new Court("IT Court", "Test Location", "Tenis", null, BigDecimal.TEN));

        var createMatchBody = "{\"title\":\"Amistoso\",\"description\":\"Partido de prueba\","
                + "\"dateTime\":\"" + LocalDateTime.now().plusDays(1) + "\",\"status\":\"OPEN\","
                + "\"maxPlayers\":3,\"currentPlayers\":1,\"courtId\":" + court.getId()
                + ",\"createdById\":" + creator.profileId() + "}";
        MvcResult createMatchResult = mockMvc.perform(post("/api/v1/matches")
                        .header("Authorization", creator.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createMatchBody))
                .andReturn();
        assertTrue(createMatchResult.getResponse().getStatus() < 400,
                "match creation should succeed, got " + createMatchResult.getResponse().getStatus());
        var matchId = objectMapper.readTree(createMatchResult.getResponse().getContentAsString()).get("id").asLong();

        // Buddy joins directly (existing USM10 instant-join path) so the match now has 2 participants.
        MvcResult buddyJoin = mockMvc.perform(post("/api/v1/matches/" + matchId + "/join")
                        .header("Authorization", buddy.bearer()))
                .andReturn();
        assertTrue(buddyJoin.getResponse().getStatus() < 400,
                "buddy join should succeed, got " + buddyJoin.getResponse().getStatus());

        // Requester asks to join via the consensus flow instead of the instant-join endpoint.
        MvcResult createRequest = mockMvc.perform(post("/api/v1/matches/" + matchId + "/join-requests")
                        .header("Authorization", requester.bearer()))
                .andReturn();
        assertEquals(201, createRequest.getResponse().getStatus(),
                "join request creation should succeed, got " + createRequest.getResponse().getStatus());
        JsonNode requestJson = objectMapper.readTree(createRequest.getResponse().getContentAsString());
        var requestId = requestJson.get("id").asLong();
        assertEquals("PENDING", requestJson.get("status").asText());

        // Creator approves alone: consensus is NOT complete yet (buddy hasn't approved).
        MvcResult creatorApprove = mockMvc.perform(post(
                        "/api/v1/matches/" + matchId + "/join-requests/" + requestId + "/approve")
                        .header("Authorization", creator.bearer()))
                .andReturn();
        assertTrue(creatorApprove.getResponse().getStatus() < 400,
                "creator approval should succeed, got " + creatorApprove.getResponse().getStatus());
        assertEquals("PENDING", objectMapper.readTree(creatorApprove.getResponse().getContentAsString()).get("status").asText());

        MvcResult matchAfterOneApproval = mockMvc.perform(get("/api/v1/matches/" + matchId)
                        .header("Authorization", requester.bearer()))
                .andReturn();
        assertEquals(2, objectMapper.readTree(matchAfterOneApproval.getResponse().getContentAsString())
                        .get("participants").size(),
                "requester must NOT be a participant until every current participant approves");

        // Buddy approves too: consensus reached, requester is finally added.
        MvcResult buddyApprove = mockMvc.perform(post(
                        "/api/v1/matches/" + matchId + "/join-requests/" + requestId + "/approve")
                        .header("Authorization", buddy.bearer()))
                .andReturn();
        assertTrue(buddyApprove.getResponse().getStatus() < 400,
                "buddy approval should succeed, got " + buddyApprove.getResponse().getStatus());
        assertEquals("APPROVED", objectMapper.readTree(buddyApprove.getResponse().getContentAsString()).get("status").asText());

        MvcResult matchAfterConsensus = mockMvc.perform(get("/api/v1/matches/" + matchId)
                        .header("Authorization", requester.bearer()))
                .andReturn();
        assertEquals(3, objectMapper.readTree(matchAfterConsensus.getResponse().getContentAsString())
                        .get("participants").size(),
                "requester must be a participant once consensus is reached");

        // The requester must have received a real backend notification, not a client-side simulation.
        MvcResult requesterNotifications = mockMvc.perform(get("/api/v1/notifications/me")
                        .header("Authorization", requester.bearer()))
                .andReturn();
        var notifications = objectMapper.readTree(requesterNotifications.getResponse().getContentAsString());
        boolean hasApprovalNotification = false;
        for (JsonNode notification : notifications) {
            if ("MATCH_JOIN_APPROVED".equals(notification.get("type").asText())) {
                hasApprovalNotification = true;
            }
        }
        assertTrue(hasApprovalNotification, "requester should have received a MATCH_JOIN_APPROVED notification");
    }

    private AuthedPlayer registerPlayer(String username, String name) throws Exception {
        var password = "123456";
        var signUpBody = "{\"username\":\"" + username + "\",\"password\":\"" + password
                + "\",\"roles\":[\"ROLE_USER\"]}";
        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpBody))
                .andReturn();

        var signInBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        MvcResult signInResult = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInBody))
                .andReturn();
        JsonNode signInJson = objectMapper.readTree(signInResult.getResponse().getContentAsString());
        var token = signInJson.get("token").asText();
        var iamUserId = signInJson.get("id").asLong();
        var bearer = "Bearer " + token;

        var profileBody = "{\"name\":\"" + name + "\",\"email\":\"" + username + "@test.com\","
                + "\"phone\":\"999999999\",\"imageUrl\":\"https://example.com/a.png\",\"userId\":" + iamUserId + "}";
        MvcResult createProfile = mockMvc.perform(post("/api/v1/user-profiles")
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(profileBody))
                .andReturn();
        var profileId = objectMapper.readTree(createProfile.getResponse().getContentAsString()).get("id").asLong();

        return new AuthedPlayer(bearer, profileId);
    }
}
