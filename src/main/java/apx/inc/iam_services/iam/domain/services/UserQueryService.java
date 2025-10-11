package apx.inc.iam_services.iam.domain.services;

import apx.inc.iam_services.iam.domain.model.aggregates.User;
import apx.inc.iam_services.iam.domain.model.queries.GetAllUsersQuery;
import apx.inc.iam_services.iam.domain.model.queries.GetUserByIdQuery;
import apx.inc.iam_services.iam.domain.model.queries.GetUserByUserNameQuery;
import apx.inc.iam_services.iam.domain.model.queries.GetUsersByCourseIdQuery;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    List<User> handle(GetAllUsersQuery getAllUsersQuery);

    Optional<User> handle(GetUserByIdQuery getUserByIdQuery);

    Optional<User> handle(GetUserByUserNameQuery getUserByUserNameQuery);

    List<User> handle(GetUsersByCourseIdQuery getUsersByCourseIdQuery);
}
