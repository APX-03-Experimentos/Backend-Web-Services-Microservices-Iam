package apx.inc.iam_services.iam.domain.model.commands;

import apx.inc.iam_services.iam.domain.model.entities.Role;

import java.util.List;

public record CreateUserCommand(
        String userName,
        String password,
        List<Role> roles
) {
    public CreateUserCommand {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }
    }
}
