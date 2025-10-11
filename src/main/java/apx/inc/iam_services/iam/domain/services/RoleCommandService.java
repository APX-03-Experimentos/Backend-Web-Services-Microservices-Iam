package apx.inc.iam_services.iam.domain.services;

import apx.inc.iam_services.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {

    void handle(SeedRolesCommand seedRolesCommand);

}
