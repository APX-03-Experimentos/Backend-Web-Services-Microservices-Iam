package apx.inc.iam_services.iam.infrastructure.persistence.jpa.repositories;

import apx.inc.iam_services.iam.domain.model.aggregates.User;
import apx.inc.iam_services.iam.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndPassword(String userName,String password);

    // Additional query methods can be defined here if needed


    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    //List<ProfileInGroup> findProfilesInGroupsByIdAndProfilesInGroups(Long userId, Long groupId);
}
