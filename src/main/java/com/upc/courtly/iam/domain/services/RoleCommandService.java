package com.upc.courtly.iam.domain.services;

import com.upc.courtly.iam.domain.model.commands.SeedRolesCommand;

/**
 * Role command service
 * <p>
 *     This interface represents the service to handle role commands.
 * </p>
 */
public interface RoleCommandService {
    /**
     * Handle seed roles command
     * @param command the {@link SeedRolesCommand} command
     *
     */
    void handle(SeedRolesCommand command);
}
