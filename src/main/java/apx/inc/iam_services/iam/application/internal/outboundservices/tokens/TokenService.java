package apx.inc.iam_services.iam.application.internal.outboundservices.tokens;

import org.springframework.security.core.Authentication;

import java.util.List;

public interface TokenService {
    //String generateToken(String userName);

    String getUserNameFromToken(String token);

    boolean validateToken(String token);

    String generateToken(Authentication authentication);

    String generateToken(String username, Long userId, List<String> roles);

}
