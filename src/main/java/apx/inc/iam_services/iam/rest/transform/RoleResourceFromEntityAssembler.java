package apx.inc.iam_services.iam.rest.transform;

import apx.inc.iam_services.iam.domain.model.entities.Role;
import apx.inc.iam_services.iam.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role roleEntity) {

        return new RoleResource(
                roleEntity.getId(),
                roleEntity.getStringName()
        );
    }
}
