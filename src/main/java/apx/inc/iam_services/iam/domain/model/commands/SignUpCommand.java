package apx.inc.iam_services.iam.domain.model.commands;

import apx.inc.iam_services.iam.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.Set;

public record SignUpCommand(
        String username,
        String password,
        List<Roles> roles
) {
    public SignUpCommand {
        if (username==null||username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password==null||password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (roles==null||roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }
    }
}
