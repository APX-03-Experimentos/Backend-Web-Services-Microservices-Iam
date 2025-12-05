package apx.inc.iam_services.iam.application.internal.queryservices;

import apx.inc.iam_services.iam.domain.model.aggregates.User;
import apx.inc.iam_services.iam.domain.model.queries.*;
import apx.inc.iam_services.iam.domain.services.UserQueryService;
import apx.inc.iam_services.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery getUserByIdQuery) {
        return userRepository.findById(getUserByIdQuery.userId());
    }

    @Override
    public Optional<User> handle(GetUserByUsernameQuery getUserByUsernameQuery) {
        return userRepository.findByUsername(getUserByUsernameQuery.username());
    }

    @Override
    public List<User> handle(GetAllUsersQuery getAllUsersQuery) {
        return userRepository.findAll();
    }

    @Override
    public List<User> handle(GetUsersByCourseIdQuery query) {
        return userRepository.findAll().stream()
                .filter(user -> user.getStudentInCourseIds().contains(query.courseId()))
                .toList();
    }


}
