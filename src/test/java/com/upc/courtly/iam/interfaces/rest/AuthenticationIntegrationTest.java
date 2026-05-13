package com.upc.courtly.iam.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.courtly.iam.domain.model.aggregates.User;
import com.upc.courtly.iam.domain.model.commands.SignInCommand;
import com.upc.courtly.iam.domain.model.commands.SignUpCommand;
import com.upc.courtly.iam.domain.services.UserCommandService;
import com.upc.courtly.iam.interfaces.rest.resources.SignInResource;
import com.upc.courtly.iam.interfaces.rest.resources.SignUpResource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationIntegrationTest {

    @Mock
    private UserCommandService userCommandService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthenticationController(userCommandService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("Debería registrar un usuario correctamente")
    void shouldRegisterUserSuccessfully() throws Exception {
        SignUpResource request = new SignUpResource(
                "Marco Perez",
                "marcoperez123",
                List.of("ROLE_USER")
        );

        User user = new User();
        user.setUsername("Marco Perez");
        user.setPassword("marcoperez123");

        when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Marco Perez"));
    }

    @Test
    @DisplayName("Debería iniciar sesión correctamente")
    void shouldLoginSuccessfully() throws Exception {

        SignInResource request = new SignInResource(
                "Marco Perez",
                "marcoperez123"
        );

        User user = new User();
        user.setUsername("Marco Perez");
        user.setPassword("marcoperez123");

        ImmutablePair<User, String> authResult =
                new ImmutablePair<>(user, "jwt-token-test");

        when(userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.of(authResult));

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Marco Perez"))
                .andExpect(jsonPath("$.token").value("jwt-token-test"));
    }
}