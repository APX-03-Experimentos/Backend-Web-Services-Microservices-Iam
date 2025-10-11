package apx.inc.iam_services.iam.rest.resources;

import apx.inc.iam_services.iam.domain.model.valueobjects.Roles;

import java.util.List;

public record UserResource(
        Long id,
        String userName,
        List<Roles> roles,
        List<Long> courseIds    // ← Lista de cursos del usuario
) { }
