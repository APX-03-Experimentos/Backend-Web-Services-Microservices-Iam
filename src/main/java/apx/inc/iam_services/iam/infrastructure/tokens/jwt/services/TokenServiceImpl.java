package apx.inc.iam_services.iam.infrastructure.tokens.jwt.services;

import apx.inc.iam_services.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import apx.inc.iam_services.iam.infrastructure.tokens.jwt.BearerTokenService;
import apx.inc.iam_services.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class TokenServiceImpl implements BearerTokenService {
    private final Logger LOGGER= LoggerFactory.getLogger(TokenServiceImpl.class);

    private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer";
    private static final int TOKEN_BEGIN_INDEX = 7;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value(("${jwt.expiration-days:7}"))
    private int tokenExpirationTimeInDays;


    private String buildTokenWithDefaultParameters(String username, Long userId) {
        var issuedAt = new Date();
        var expirationDate = DateUtils.addDays(issuedAt, tokenExpirationTimeInDays);
        var key = getSigningKey();

        return Jwts.builder()
                .subject(username)
                .claim("user_id", userId)  // ✅ AGREGAR userId en los claims
                .claim("username", username)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(getSigningKey()).build().parseClaimsJws(token).getPayload();
    }



    private boolean isTokenPresentIn(String authorizationParameter){
        return StringUtils.hasText(authorizationParameter);
    }

    private boolean isBearerTokenIn(String authorizationParameter){
        return authorizationParameter.startsWith(BEARER_TOKEN_PREFIX);
    }

    private String extractTokenFrom(String authorizationParameter){
        return authorizationParameter.substring(TOKEN_BEGIN_INDEX);
    }

    private String getAuthorizationParameterFrom(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_PARAMETER_NAME);
    }



    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        String parameter = getAuthorizationParameterFrom(request);
        if (isTokenPresentIn(parameter) && isBearerTokenIn(parameter)) {
            return extractTokenFrom(parameter);
        }
        return null;
    }

    @Override
    public String generateToken(Authentication authentication) {
        // Necesitas extraer el userId del UserDetails
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return buildTokenWithDefaultParameters(authentication.getName(), userDetails.getId());
    }

    @Override
    public String generateToken(String username) {
        // Para este método necesitas obtener el userId de otra forma
        // O eliminar este método si no lo usas
        throw new UnsupportedOperationException("Use generateToken with userId");
    }
    @Override
    public String getUserNameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            LOGGER.info("Token is valid");
            return true;
        }  catch (SignatureException e) {
            LOGGER.error("Invalid JSON Web Token Signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JSON Web Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JSON Web Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JSON Web Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JSON Web Token claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
