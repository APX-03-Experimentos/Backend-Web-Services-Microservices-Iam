package apx.inc.iam_services.iam.rest.transform;

import apx.inc.iam_services.iam.domain.model.commands.CreateUserCommand;
import apx.inc.iam_services.iam.rest.resources.CreateUserResource;

public class CreateUserCommandFromResourceAssembler {
    public static CreateUserCommand toCommandFromResource(CreateUserResource createUserResource) {
        return new CreateUserCommand(
                createUserResource.username(),
                createUserResource.password(),
                createUserResource.roles().stream().toList());
    }
}
