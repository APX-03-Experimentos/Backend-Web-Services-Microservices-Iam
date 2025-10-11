package apx.inc.iam_services.iam.rest.transform;

import apx.inc.iam_services.iam.domain.model.commands.UpdateUserCommand;
import apx.inc.iam_services.iam.rest.resources.UpdateUserResource;

public class UpdateUserCommandFromResourceAssembler {
    public static UpdateUserCommand toCommandFromResource(UpdateUserResource updateUserResource){
        return new UpdateUserCommand(
                updateUserResource.userName(),
                updateUserResource.password());
    }
}
