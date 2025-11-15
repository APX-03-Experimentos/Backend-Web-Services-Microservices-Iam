package apx.inc.iam_services.iam.rest;

import apx.inc.iam_services.iam.domain.model.entities.Role;
import apx.inc.iam_services.iam.domain.model.queries.GetAllRolesQuery;
import apx.inc.iam_services.iam.domain.services.RoleCommandService;
import apx.inc.iam_services.iam.domain.services.RoleQueryService;
import apx.inc.iam_services.iam.rest.resources.RoleResource;
import apx.inc.iam_services.iam.rest.transform.RoleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name= "Roles", description = "Operations related to roles management")
public class RolesController {
    private static final Logger logger = LoggerFactory.getLogger(RolesController.class);

    private final RoleCommandService roleCommandService;
    private final RoleQueryService roleQueryService;

    public RolesController(RoleCommandService roleCommandService, RoleQueryService roleQueryService) {
        this.roleCommandService = roleCommandService;
        this.roleQueryService = roleQueryService;
        logger.info("✅ RolesController initialized and registered!");
        logger.info("✅ RoleCommandService: {}", roleCommandService != null ? "INJECTED" : "NULL");
        logger.info("✅ RoleQueryService: {}", roleQueryService != null ? "INJECTED" : "NULL");
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicTest() {
        logger.info("🟡 GET /api/v1/roles/test called");
        return ResponseEntity.ok("✅ Roles endpoint is accessible without auth");
    }

    // Agrega este endpoint de diagnóstico primero
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("🟡 GET /api/v1/roles/ping called - Controller is working!");
        return ResponseEntity.ok("PONG - RolesController is working at: " + System.currentTimeMillis());
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Fetches all available roles in the system")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved roles"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
            }
    )
    public ResponseEntity<List<RoleResource>> getAllRoles() {
        logger.info("🟡 GET /api/v1/roles endpoint called!");

        try {
            //create a query to get all roles
            GetAllRolesQuery getAllRolesQuery = new GetAllRolesQuery();
            logger.info("🔍 Query created: {}", getAllRolesQuery);

            //handle the query using the roleQueryService
            List<Role> roles = roleQueryService.handle(getAllRolesQuery);
            logger.info("🔍 Roles retrieved: {}", roles != null ? roles.size() : "NULL");

            //verify if roles are not null or empty
            if (roles == null || roles.isEmpty()) {
                logger.warn("⚠️ No roles found - returning 204");
                return ResponseEntity.noContent().build();
            }

            //map the roles to RoleResource
            var roleResources = roles.stream()
                    .map(RoleResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            logger.info("✅ Successfully returning {} roles", roleResources.size());
            return ResponseEntity.ok(roleResources);

        } catch (Exception e) {
            logger.error("❌ Error in getAllRoles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}