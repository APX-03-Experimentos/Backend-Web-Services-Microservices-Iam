package apx.inc.iam_services.iam.rest.transform;

import apx.inc.iam_services.iam.domain.model.commands.SignUpCommand;
import apx.inc.iam_services.iam.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource signUpResource) {
        return new SignUpCommand(
                signUpResource.username(),
                signUpResource.password(),
                signUpResource.roles()
        );
    }
}
