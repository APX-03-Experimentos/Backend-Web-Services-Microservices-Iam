package apx.inc.iam_services.iam.rest.transform;

import apx.inc.iam_services.iam.domain.model.commands.SignInCommand;
import apx.inc.iam_services.iam.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource){
        return new SignInCommand(
                signInResource.username(),
                signInResource.password()
        );
    }
}
