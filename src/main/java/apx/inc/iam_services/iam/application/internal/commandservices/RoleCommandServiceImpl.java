package apx.inc.iam_services.iam.application.internal.commandservices;

import apx.inc.iam_services.iam.domain.model.commands.SeedRolesCommand;
import apx.inc.iam_services.iam.domain.model.entities.Role;
import apx.inc.iam_services.iam.domain.model.valueobjects.Roles;
import apx.inc.iam_services.iam.domain.services.RoleCommandService;
import apx.inc.iam_services.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RoleCommandServiceImpl implements RoleCommandService {
    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand seedRolesCommand) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(Roles.valueOf(role.name())));
            }
        });
    }
}
