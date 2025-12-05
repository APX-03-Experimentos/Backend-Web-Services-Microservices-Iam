package apx.inc.iam_services.iam.rest.resources;

import apx.inc.iam_services.iam.domain.model.valueobjects.Roles;

import java.util.List;

public record SignUpResource(
        String username,
        String password,
        List<Roles> roles
) {
}
