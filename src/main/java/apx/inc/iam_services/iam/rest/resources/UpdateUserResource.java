package apx.inc.iam_services.iam.rest.resources;

public record UpdateUserResource(
        String userName,
        String password
) {
    public UpdateUserResource {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
    }
}
