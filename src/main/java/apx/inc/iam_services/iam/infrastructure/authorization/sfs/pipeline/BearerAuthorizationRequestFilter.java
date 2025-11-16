package apx.inc.iam_services.iam.infrastructure.authorization.sfs.pipeline;

import apx.inc.iam_services.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import apx.inc.iam_services.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Bearer Authorization Request Filter.
 * <p>
 * This class is responsible for filtering requests and setting the user authentication.
 * It extends the OncePerRequestFilter class.
 * </p>
 * @see OncePerRequestFilter
 */
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);
    private final BearerTokenService tokenService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/authentication",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars",
            "/error"
    );

    @Qualifier("defaultUserDetailsService")
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * This method is responsible for filtering requests and setting the user authentication.
     * @param request The request object.
     * @param response The response object.
     * @param filterChain The filter chain object.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = tokenService.getBearerTokenFrom(request);
            LOGGER.info("🔐 Processing request to: {}", request.getRequestURI());
            LOGGER.info("🔐 Token present: {}", token != null);

            if (token != null && tokenService.validateToken(token)) {
                String username = tokenService.getUserNameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
                LOGGER.info("✅ User authenticated: {}", username);
                filterChain.doFilter(request, response); // ✅ Solo continuar si está autenticado
            } else {
                LOGGER.warn("❌ Invalid or missing token for: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Valid Bearer token required\"}");
                // ❌ NO llamar filterChain.doFilter() - la petición se rechaza aquí
            }

        } catch (Exception e) {
            LOGGER.error("❌ Authentication error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication failed\", \"message\": \"" + e.getMessage() + "\"}");
            // ❌ NO llamar filterChain.doFilter() - la petición se rechaza aquí
        }
    }
}
