package apx.inc.iam_services.iam.rest;

import apx.inc.iam_services.iam.application.internal.outboundservices.tokens.TokenService;
import apx.inc.iam_services.iam.domain.services.UserCommandService;
import apx.inc.iam_services.iam.rest.resources.AuthenticatedUserResource;
import apx.inc.iam_services.iam.rest.resources.SignInResource;
import apx.inc.iam_services.iam.rest.resources.SignUpResource;
import apx.inc.iam_services.iam.rest.resources.UserResource;
import apx.inc.iam_services.iam.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import apx.inc.iam_services.iam.rest.transform.SignInCommandFromResourceAssembler;
import apx.inc.iam_services.iam.rest.transform.SignUpCommandFromResourceAssembler;
import apx.inc.iam_services.iam.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/authentication" ,produces= MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Authentication", description = "Authentication operations")
public class AuthenticationController {
    private final UserCommandService userCommandService;
    private final TokenService tokenService;

    public AuthenticationController(UserCommandService userCommandService, TokenService tokenService) {
        this.userCommandService = userCommandService;
        this.tokenService = tokenService;

    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        System.out.println("✅ GET /api/v1/authentication/test called!");
        return ResponseEntity.ok("✅ AuthenticationController is WORKING!");
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in user", description = "Sign in user with user name and password")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User signed in successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Not Found - User does not exist"),
            }
    )
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource signInResource){

        var signInCommand= SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedUser = userCommandService.handle(signInCommand);

        if (authenticatedUser.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
        return ResponseEntity.ok(authenticatedUserResource);
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Sign-up", description = "Sign-up with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.")})
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var user = userCommandService.handle(signUpCommand);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);

    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validates if a JWT token is valid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Token is invalid")
    })
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Extraer el token del header "Bearer <token>"
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                boolean isValid = tokenService.validateToken(jwtToken);
                return ResponseEntity.ok(isValid);
            }
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

}
