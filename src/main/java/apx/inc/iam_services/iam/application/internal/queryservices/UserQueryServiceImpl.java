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
    public Optional<User> handle(GetUserByUserNameQuery getUserByUserNameQuery) {
        return userRepository.findByUserName(getUserByUserNameQuery.userName());
    }

    @Override
    public List<User> handle(GetAllUsersQuery getAllUsersQuery) {
        return userRepository.findAll();
    }

//    @Override
//    public Optional<User> handle(GetUserByEmailAndPasswordQuery getUserByEmailAndPasswordQuery) {
//        return userRepository.findByEmailAndPassword(getUserByEmailAndPasswordQuery.email(), getUserByEmailAndPasswordQuery.password());
//    }

//    @Override
//    public Optional<User> handle(GetUserByEmailQuery getUserByEmailQuery) {
//        return userRepository.findByEmail(getUserByEmailQuery.email());
//    }

//    @Override
//    public Optional<ProfileInGroup> handle(GetProfilesInGroupsByGroupIdAndStudentIdQuery query) {
//        return userRepository.findById(query.studentId())
//                .map(User::getProfilesInGroups)
//                .orElse(List.of())
//                .stream()
//                .filter(p -> p.getGroupId().equals(query.groupId()))
//                .findFirst(); // ✅ Solo el primero, como Optional
//    }

    @Override
    public List<User> handle(GetUsersByCourseIdQuery query) {
        // ✅ SOLUCIÓN: Usar studentInCourseIds (Set<Long>) en lugar de Course entities
        return userRepository.findAll().stream()
                .filter(user -> user.getStudentInCourseIds().contains(query.courseId()))
                .toList();
    }

//    @Override
//    public Optional<String> handle(GetFullNameByIdQuery getFullNameByIdQuery) {
//        return userRepository.findById(getFullNameByIdQuery.userId())
//                .map(user -> user.getFirstName() + " " + user.getLastName());
//    }

}
