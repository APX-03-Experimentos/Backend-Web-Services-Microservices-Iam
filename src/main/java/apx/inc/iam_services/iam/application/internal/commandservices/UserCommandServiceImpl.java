package apx.inc.iam_services.iam.application.internal.commandservices;

import apx.inc.iam_services.iam.application.internal.outboundservices.hashing.HashingService;
import apx.inc.iam_services.iam.application.internal.outboundservices.tokens.TokenService;
import apx.inc.iam_services.iam.domain.model.aggregates.User;
import apx.inc.iam_services.iam.domain.model.commands.*;
import apx.inc.iam_services.iam.domain.services.UserCommandService;
import apx.inc.iam_services.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import apx.inc.iam_services.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(UserRepository userRepository, RoleRepository roleRepository, HashingService hashingService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<User> handle(UpdateUserCommand updateUserCommand, Long userId) {
        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        //Check if a user with the same email already exists
        var existingUserWithEmail = userRepository.findByUsername(updateUserCommand.username());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(userId)) {
            throw new IllegalArgumentException("User with userName " + updateUserCommand.username() + " already exists");
        }

        var userToUpdate = userOptional.get();

        // ✅ Aquí cifras la contraseña SOLO si no viene vacía
        String encodedPassword = updateUserCommand.password();
        if (encodedPassword != null && !encodedPassword.isBlank()) {
            encodedPassword = hashingService.encode(encodedPassword);
        } else {
            // Si viene vacía, mantén la actual
            encodedPassword = userToUpdate.getPassword();
        }

        // ✅ Crea un nuevo comando con la contraseña cifrada
        var commandWithEncodedPassword = new UpdateUserCommand(
                updateUserCommand.username(),
                encodedPassword
        );

        try{
            var updatedUser= userRepository.save(userToUpdate.updateUserDetails(commandWithEncodedPassword));
            return Optional.of(updatedUser);
        }catch (Exception e) {
            // Handle exception, e.g., log it or rethrow as a custom exception
            return Optional.empty();
        }
    }

    @Override
    public void handle(DeleteUserCommand deleteUserCommand) {
        if (!userRepository.existsById(deleteUserCommand.userId())) {
            throw new IllegalArgumentException("User with ID " + deleteUserCommand.userId() + " not found");
        }

        try{
            userRepository.deleteById(deleteUserCommand.userId());
        } catch (Exception e) {
            // Handle exception, e.g., log it or rethrow as a custom exception
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> handle(LeaveCourseCommand leaveCourseCommand) {
        // Check if the user exists
        var userOptional = userRepository.findById(leaveCourseCommand.userId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + leaveCourseCommand.userId() + " not found");
        }

        var user = userOptional.get();

        // ✅ SOLUCIÓN: Validar usando studentInCourseIds (Set<Long>)
        boolean belongsToCourse = user.getStudentInCourseIds().contains(leaveCourseCommand.courseId());

        if (!belongsToCourse) {
            throw new IllegalArgumentException("User does not belong to the course with ID " + leaveCourseCommand.courseId());
        }

        user.removeFromCourse(leaveCourseCommand.courseId());

        // Save the updated user
        try {
            userRepository.save(user);
            return Optional.of(user);
        } catch (Exception e) {
            throw new RuntimeException("Error while removing user from course", e);
        }
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand signInCommand) {
        var user = userRepository.findByUsername(signInCommand.username());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User with user name " + signInCommand.username() + " not found");
        }
        if (!hashingService.matches(signInCommand.password(), user.get().getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // ✅ CORREGIDO: Extraer roles y pasar al token
        var authenticatedUser = user.get();

        // Extraer roles del usuario
        List<String> roles = authenticatedUser.getUserRoles().stream()
                .map(role -> role.getStringName()) // Ajusta según tu entidad Role
                .collect(Collectors.toList());

        // ✅ Usar el nuevo método que requiere userId y roles
        var token = tokenService.generateToken(
                authenticatedUser.getUsername(),
                authenticatedUser.getId(),
                roles
        );

        return Optional.of(ImmutablePair.of(authenticatedUser, token));
    }

    @Override
    public Optional<User> handle(SignUpCommand signUpCommand) {
        if (userRepository.existsByUsername(signUpCommand.username())) {
            throw new IllegalArgumentException("User with user name " + signUpCommand.username() + " already exists");
        }
        var roles= signUpCommand.roles().stream().map(
                role->roleRepository.findByName(role)
                        .orElseThrow(() -> new IllegalArgumentException("Role " + role + " not found"))
                ).toList();
        var user = new User(signUpCommand.username(), hashingService.encode(signUpCommand.password()), roles);
        userRepository.save(user);
        return userRepository.findByUsername(signUpCommand.username());
    }








    @Override
    public void assignCourseToUser(Long userId, Long courseId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.assignToCourse(courseId);
        userRepository.save(user);
    }

    @Override
    public void removeCourseFromUser(Long userId, Long courseId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.removeFromCourse(courseId);
        userRepository.save(user);
    }
}
