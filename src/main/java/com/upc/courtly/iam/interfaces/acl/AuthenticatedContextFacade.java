package com.upc.courtly.iam.interfaces.acl;

import com.upc.courtly.coaches.domain.model.aggregates.Coach;
import com.upc.courtly.coaches.infrastructure.persistence.jpa.repositories.CoachRepository;
import com.upc.courtly.iam.domain.model.aggregates.User;
import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticatedContextFacade {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CoachRepository coachRepository;

    public AuthenticatedContextFacade(UserRepository userRepository,
                                      UserProfileRepository userProfileRepository,
                                      CoachRepository coachRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.coachRepository = coachRepository;
    }

    public User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated user found");
        }
        var username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated IAM user not found"));
    }

    public UserProfile getAuthenticatedUserProfile() {
        var user = getAuthenticatedUser();
        return userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user does not have a user profile"));
    }

    /**
     * Returns the authenticated user's profile if it exists, empty otherwise.
     * Unlike {@link #getAuthenticatedUserProfile()} this does not throw when the
     * profile is missing, so callers can return a proper 404 instead of a 500.
     */
    public Optional<UserProfile> findAuthenticatedUserProfile() {
        var user = getAuthenticatedUser();
        return userProfileRepository.findByUserId(user.getId());
    }

    public Coach getAuthenticatedCoachProfile() {
        var user = getAuthenticatedUser();
        return coachRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user does not have a coach profile"));
    }
}
