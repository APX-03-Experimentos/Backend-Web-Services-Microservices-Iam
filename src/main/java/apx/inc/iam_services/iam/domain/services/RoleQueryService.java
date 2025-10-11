package apx.inc.iam_services.iam.domain.services;

import apx.inc.iam_services.iam.domain.model.entities.Role;
import apx.inc.iam_services.iam.domain.model.queries.GetAllRolesQuery;

import java.util.List;

public interface RoleQueryService {

    List<Role> handle(GetAllRolesQuery getAllRolesQuery);
}
