package com.upc.courtly.iam.infrastructure.authorization.sfs.services;

import com.upc.courtly.iam.domain.model.aggregates.User;
import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Debería cargar los detalles del usuario si el username existe")
    void shouldLoadUserByUsernameWhenUserExists() {

        User user = new User(
                "Julio Vega",
                "123456"
        );

        when(userRepository.findByUsername("Julio Vega"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("Julio Vega");

        assertNotNull(userDetails);
        assertEquals("Julio Vega", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
    }

    @Test
    @DisplayName("Debería lanzar un error cuando el usuario no existe")
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByUsername("Luis Ramos"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("Luis Ramos")
        );
    }
}