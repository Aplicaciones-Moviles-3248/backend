package com.upc.courtly.users.interfaces.rest;

import com.upc.courtly.iam.interfaces.acl.AuthenticatedContextFacade;
import com.upc.courtly.users.domain.model.commands.DeleteUserProfileCommand;
import com.upc.courtly.users.domain.model.queries.GetAllUserProfilesQuery;
import com.upc.courtly.users.domain.model.queries.GetUserProfileByIdQuery;
import com.upc.courtly.users.domain.services.UserProfileCommandService;
import com.upc.courtly.users.domain.services.UserProfileQueryService;
import com.upc.courtly.users.interfaces.rest.resources.CreateUserProfileResource;
import com.upc.courtly.users.interfaces.rest.resources.UpdateUserProfileResource;
import com.upc.courtly.users.interfaces.rest.resources.UserProfileResource;
import com.upc.courtly.users.interfaces.rest.transform.CreateUserProfileCommandFromResourceAssembler;
import com.upc.courtly.users.interfaces.rest.transform.UpdateUserProfileCommandFromResourceAssembler;
import com.upc.courtly.users.interfaces.rest.transform.UserProfileResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/user-profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User Profiles", description = "User Profile Management Endpoints")
public class UserProfilesController {
    private final UserProfileCommandService userProfileCommandService;
    private final UserProfileQueryService userProfileQueryService;
    private final AuthenticatedContextFacade authenticatedContextFacade;

    public UserProfilesController(UserProfileCommandService userProfileCommandService,
                                  UserProfileQueryService userProfileQueryService,
                                  AuthenticatedContextFacade authenticatedContextFacade) {
        this.userProfileCommandService = userProfileCommandService;
        this.userProfileQueryService = userProfileQueryService;
        this.authenticatedContextFacade = authenticatedContextFacade;
    }

    @PostMapping
    public ResponseEntity<UserProfileResource> createUserProfile(@RequestBody CreateUserProfileResource resource) {
        var currentUser = authenticatedContextFacade.getAuthenticatedUser();
        if (resource.userId() != null && !resource.userId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create your own profile");
        }
        var command = CreateUserProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var userProfile = userProfileCommandService.handle(command);
        return userProfile.map(profile -> new ResponseEntity<>(UserProfileResourceFromEntityAssembler.toResourceFromEntity(profile), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResource> getMyUserProfile() {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        return ResponseEntity.ok(UserProfileResourceFromEntityAssembler.toResourceFromEntity(currentUserProfile));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResource>> getAllUserProfiles() {
        var query = new GetAllUserProfilesQuery();
        var userProfiles = userProfileQueryService.handle(query);
        var userProfileResources = userProfiles.stream()
                .map(UserProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(userProfileResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResource> getUserProfileById(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own profile");
        }
        var query = new GetUserProfileByIdQuery(id);
        var userProfile = userProfileQueryService.handle(query);
        return userProfile.map(profile -> ResponseEntity.ok(UserProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResource> updateUserProfile(@PathVariable Long id, @RequestBody UpdateUserProfileResource resource) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile");
        }
        var command = UpdateUserProfileCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var updatedUserProfile = userProfileCommandService.handle(command);
        return updatedUserProfile.map(profile -> ResponseEntity.ok(UserProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserProfile(@PathVariable Long id) {
        var currentUserProfile = authenticatedContextFacade.getAuthenticatedUserProfile();
        if (!currentUserProfile.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own profile");
        }
        var command = new DeleteUserProfileCommand(id);
        userProfileCommandService.handle(command);
        return ResponseEntity.ok("User deleted successfully.");
    }
}

