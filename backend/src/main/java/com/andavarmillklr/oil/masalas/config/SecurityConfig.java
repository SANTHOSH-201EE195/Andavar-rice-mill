package com.andavarmillklr.oil.masalas.config;

import com.andavarmillklr.oil.masalas.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * SecurityConfig - Configures Spring Security for the application.
 *
 * This is the CENTRAL configuration that controls:
 *   - Which endpoints are public (no login required)
 *   - Which endpoints require authentication (login required)
 *   - Which endpoints are admin-only
 *   - How passwords are encrypted
 *   - CORS settings (so the React frontend can call our API)
 *   - JWT filter integration
 *
 * Security Rules:
 *   PUBLIC (no token needed):
 *     - POST /api/auth/register     → Register a new account
 *     - POST /api/auth/login        → Login
 *     - GET  /api/products          → Browse products
 *     - GET  /api/products/{id}     → View a single product
 *     - GET  /api/reviews/**        → View reviews
 *     - GET  /api/ratings/**        → View ratings
 *     - GET  /images/**             → View product images
 *
 *   AUTHENTICATED (token required):
 *     - /api/cart/**                → Cart operations
 *     - /api/orders/**             → Place and view orders
 *     - POST /api/reviews          → Write a review
 *
 *   ADMIN ONLY (admin token required):
 *     - /api/admin/**              → Admin panel operations
 *     - POST/PUT/DELETE /api/products → Manage products
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** The JWT filter that processes Bearer tokens */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor — Spring injects the JWT filter automatically.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Password Encoder Bean — uses BCrypt algorithm to hash passwords.
     *
     * BCrypt is the industry standard for password hashing because:
     *   - It's slow on purpose (makes brute-force attacks impractical)
     *   - It includes a random "salt" (so identical passwords produce different hashes)
     *   - It's built into Spring Security
     *
     * @return PasswordEncoder that uses BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Manager Bean — used by the AuthService to authenticate users.
     *
     * @param authConfig Spring's authentication configuration
     * @return The authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * CORS Configuration — allows the React frontend to call our backend API.
     *
     * Without this, the browser would block all requests from http://localhost:5173
     * (the Vite dev server) to http://localhost:8080 (our Spring Boot server)
     * because they are on different ports (cross-origin).
     *
     * @return CorsConfigurationSource with our CORS rules
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from these origins (read from env or fallback to defaults)
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        } else {
            configuration.setAllowedOriginPatterns(Arrays.asList(
                    "http://localhost:*",              // Any local dev server
                    "https://andavar.netlify.app",     // Default Netlify URL
                    "*"                                // Allow all temporarily (configurable via env)
            ));
        }

        // Allow these HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow these headers in requests
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));

        // Allow the frontend to read these response headers
        configuration.setExposedHeaders(List.of("Authorization"));

        // Allow cookies and authorization headers to be sent
        configuration.setAllowCredentials(true);

        // Apply CORS rules to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Security Filter Chain — the main security configuration.
     *
     * This method defines ALL the security rules for the application.
     *
     * @param http Spring Security's HttpSecurity builder
     * @return The configured SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with our configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF (not needed for REST APIs with JWT)
                // CSRF protection is for server-rendered forms, not for API calls
                .csrf(csrf -> csrf.disable())

                // Set session management to STATELESS
                // This means Spring will NOT create HTTP sessions.
                // Each request must include a JWT token for authentication.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Define authorization rules for different endpoints
                .authorizeHttpRequests(auth -> auth
                        // ===== PUBLIC ENDPOINTS (no login required) =====
                        .requestMatchers("/api/auth/**").permitAll()           // Login & Register
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()  // Browse products
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()   // View reviews
                        .requestMatchers(HttpMethod.GET, "/api/ratings/**").permitAll()   // View ratings
                        .requestMatchers("/images/**").permitAll()             // Product images
                        .requestMatchers("/product-images/**").permitAll()     // Uploaded images

                        // ===== ADMIN-ONLY ENDPOINTS =====
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ROLE_ADMIN")

                        // ===== ALL OTHER ENDPOINTS REQUIRE AUTHENTICATION =====
                        .anyRequest().authenticated()
                )

                // Add our JWT filter BEFORE Spring's default authentication filter
                // This way, our filter processes the JWT token first
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
