package com.upc.courtly.iam.infrastructure.authorization.sfs.pipeline;

import com.upc.courtly.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.upc.courtly.iam.infrastructure.tokens.jwt.BearerTokenService;
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


    @Qualifier("defaultUserDetailsService")
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * This method is responsible for filtering requests and setting the user authentication.
     * @param request The request object.
     * @param response The response object.
     * @param filterChain The filter chain object.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Skip bearer token processing for authentication endpoints (sign-in / sign-up)
        var path = request.getRequestURI();
        if (path != null && (path.startsWith("/api/v1/authentication") || path.startsWith("/api/v1/auth"))) {
            LOGGER.info("Skipping bearer filter for authentication endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = tokenService.getBearerTokenFrom(request);
            if (token != null) {
                if (tokenService.validateToken(token)) {
                    String username = tokenService.getUsernameFromToken(token);
                    var userDetails = userDetailsService.loadUserByUsername(username);
                    SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
                } else {
                    // A present-but-invalid token is a real failure (expired, or signed with a
                    // different key than this instance validates with). Log it so the cause is
                    // visible in the deployment logs instead of surfacing as an opaque 401.
                    LOGGER.warn("Rejected bearer token for {}: token failed validation", path);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot set user authentication for {}: {}", path, e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
