package com.upc.courtly.iam.infrastructure.persistence.seed;

import com.upc.courtly.iam.domain.model.entities.Role;
import com.upc.courtly.iam.domain.model.valueobjects.Roles;
import com.upc.courtly.iam.domain.model.aggregates.User;
import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.upc.courtly.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.upc.courtly.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import com.upc.courtly.users.domain.model.aggregates.UserProfile;
import com.upc.courtly.users.infrastructure.persistence.jpa.repositories.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Data seeder that inserts default roles and an admin user if they do not exist.
 * This runs on application startup.
 */
@Component
@Profile("!test")
@Transactional
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final BCryptHashingService hashingService;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      UserProfileRepository userProfileRepository,
                      BCryptHashingService hashingService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.hashingService = hashingService;
    }

    @Override
    public void run(String... args) {
        // Ensure roles exist
        ensureRole(Roles.ROLE_USER);
        ensureRole(Roles.ROLE_ADMIN);
        ensureRole(Roles.ROLE_INSTRUCTOR);

        // Ensure admin user exists
        if (!userRepository.existsByUsername("admin")) {
            var adminRole = roleRepository.findByName(Roles.ROLE_ADMIN).orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_ADMIN)));
            var encoded = hashingService.encode("admin");
            var admin = new User("admin", encoded, List.of(adminRole));
            userRepository.save(admin);
        }

        // Ensure a demo player exists WITH a linked profile, so the pre-filled
        // demo credentials in the mobile app work out of the box and GET
        // /user-profiles/me never fails for the demo account.
        ensureDemoPlayer("fabricio", "123456", "Fabricio Demo",
                "fabricio@courtly.app", "+51 999 000 111");
    }

    private void ensureDemoPlayer(String username, String rawPassword, String name,
                                  String email, String phone) {
        var userRole = roleRepository.findByName(Roles.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(Roles.ROLE_USER)));

        var user = userRepository.findByUsername(username).orElseGet(() -> {
            var encoded = hashingService.encode(rawPassword);
            return userRepository.save(new User(username, encoded, List.of(userRole)));
        });

        // Link a profile only if the account does not already have one.
        if (!userProfileRepository.existsByUserId(user.getId())
                && !userProfileRepository.existsByEmail(email)) {
            var profile = new UserProfile(name, email, phone, user);
            userProfileRepository.save(profile);
        }
    }

    private void ensureRole(Roles role) {
        if (!roleRepository.existsByName(role)) {
            roleRepository.save(new Role(role));
        }
    }
}
