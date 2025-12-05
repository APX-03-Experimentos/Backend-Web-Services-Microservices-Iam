package apx.inc.iam_services.iam.rest.transform;

import apx.inc.iam_services.iam.domain.model.aggregates.User;
import apx.inc.iam_services.iam.domain.model.entities.Role;

import apx.inc.iam_services.iam.rest.resources.UserResource;

import java.util.List;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(
                user.getId(),
                user.getUsername(),
                user.getUserRoles().stream().map(Role::getName).toList(),
                List.copyOf(user.getStudentInCourseIds())
        );
    }

}
