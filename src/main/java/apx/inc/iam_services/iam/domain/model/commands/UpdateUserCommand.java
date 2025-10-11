package apx.inc.iam_services.iam.domain.model.commands;

public record UpdateUserCommand(
        String userName,
        String password) {
    public UpdateUserCommand{
        if (userName==null || userName.isBlank() ) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password==null || password.isBlank() ) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
