package com.andavarmillklr.oil.masalas.security;

import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * JwtAuthenticationFilter - Intercepts every HTTP request to check for a JWT token.
 *
 * This filter runs BEFORE any controller method. It does the following:
 *
 * 1. Check if the request has an "Authorization" header with a Bearer token
 * 2. If yes, validate the token using JwtTokenProvider
 * 3. If the token is valid, extract the user ID and load the user from database
 * 4. Set the user as "authenticated" in Spring Security's context
 * 5. If no token or invalid token, the request continues as "anonymous"
 *
 * How the frontend sends the token:
 *   fetch('/api/cart', {
 *     headers: {
 *       'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9...'
 *     }
 *   })
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Used to validate tokens and extract user info */
    private final JwtTokenProvider jwtTokenProvider;

    /** Used to load the full user object from the database */
    private final UserRepository userRepository;

    /**
     * Constructor — Spring automatically injects the dependencies.
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * This method runs for EVERY incoming request.
     * It extracts the JWT token, validates it, and sets up authentication.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        try {
            // Step 1: Extract the JWT token from the Authorization header
            String token = getTokenFromRequest(request);

            // Step 2: If we have a token and it's valid, authenticate the user
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

                // Step 3: Get the user ID and role from the token
                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);

                // Step 4: Load the user from the database to confirm they still exist
                Optional<User> userOptional = userRepository.findById(userId);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    // Step 5: Create an authentication object with the user's role
                    // This tells Spring Security "this user is authenticated and has this role"
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,                                                    // The principal (user object)
                                    null,                                                    // No credentials needed (already authenticated via JWT)
                                    Collections.singletonList(new SimpleGrantedAuthority(role)) // User's authorities/roles
                            );

                    // Step 6: Store the authentication in Spring Security's context
                    // Now any controller can access the authenticated user
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // If anything goes wrong with JWT processing, log it and continue
            // The request will be treated as "anonymous" (unauthenticated)
            System.err.println("Could not set user authentication: " + ex.getMessage());
        }

        // Continue processing the request (pass it to the next filter or controller)
        filterChain.doFilter(request, response);
    }

    /**
     * Extract the JWT token from the "Authorization" header.
     *
     * The header format is: "Bearer eyJhbGciOiJIUzI1NiJ9..."
     * We need to remove the "Bearer " prefix to get just the token.
     *
     * @param request The incoming HTTP request
     * @return The JWT token string, or null if not present
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check if the header exists and starts with "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Remove "Bearer " prefix (7 characters) to get the actual token
            return bearerToken.substring(7);
        }

        return null;
    }
}
