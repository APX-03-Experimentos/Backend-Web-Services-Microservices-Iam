package apx.inc.iam_services.iam.rest.resources;

import apx.inc.iam_services.iam.domain.model.entities.Role;

import java.util.Set;

public record CreateUserResource(
        String username,
        String password,
        Set<Role> roles
) {
    public CreateUserResource {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }
    }
}
