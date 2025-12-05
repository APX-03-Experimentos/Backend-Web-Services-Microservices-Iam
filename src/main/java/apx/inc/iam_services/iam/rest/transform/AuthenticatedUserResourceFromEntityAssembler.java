package apx.inc.iam_services.iam.rest.transform;

import apx.inc.iam_services.iam.domain.model.aggregates.User;
import apx.inc.iam_services.iam.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(
                user.getId(),
                user.getUsername(),
                token,
                user.getUserRoles().stream().map(
                        role -> role.getName().name()
                ).toList()
        );
    }
}
