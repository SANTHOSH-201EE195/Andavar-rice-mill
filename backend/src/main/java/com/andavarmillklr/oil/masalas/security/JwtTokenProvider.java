package com.andavarmillklr.oil.masalas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtTokenProvider - Responsible for creating and validating JWT tokens.
 *
 * JWT (JSON Web Token) is a secure way to transmit user identity between
 * the frontend and backend. Here's how it works:
 *
 * 1. User logs in with mobile + password
 * 2. Server creates a JWT token containing user ID and role
 * 3. Frontend stores this token in localStorage
 * 4. Frontend sends the token in every request's "Authorization" header
 * 5. Server validates the token and extracts the user info
 *
 * The token is signed with a secret key, so it cannot be tampered with.
 * If someone modifies the token, the signature check will fail.
 */
@Component
public class JwtTokenProvider {

    /** Secret key from application.properties — used to sign tokens */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** Token expiration time in milliseconds from application.properties */
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Create the signing key from our secret string.
     * The key must be long enough (at least 256 bits for HS256).
     *
     * @return SecretKey used for signing and verifying tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate a new JWT token for a user.
     *
     * The token contains:
     *   - subject: the user's ID (as a string)
     *   - role: the user's role ("ROLE_USER" or "ROLE_ADMIN")
     *   - issuedAt: when the token was created
     *   - expiration: when the token expires (24 hours from now)
     *
     * @param userId The user's database ID
     * @param role   The user's role
     * @return The JWT token string (e.g., "eyJhbGciOiJIUzI1NiJ9...")
     */
    public String generateToken(Long userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))          // Who this token belongs to
                .claim("role", role)                       // User's role for authorization
                .issuedAt(now)                             // When the token was created
                .expiration(expiryDate)                    // When the token expires
                .signWith(getSigningKey())                 // Sign with our secret key
                .compact();                                // Build the token string
    }

    /**
     * Extract the user ID from a JWT token.
     *
     * @param token The JWT token string
     * @return The user's ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extract the user's role from a JWT token.
     *
     * @param token The JWT token string
     * @return The user's role (e.g., "ROLE_USER")
     */
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    /**
     * Validate whether a JWT token is valid.
     *
     * A token is invalid if:
     *   - It has been tampered with (signature mismatch)
     *   - It has expired
     *   - It is malformed (not a proper JWT format)
     *   - It is empty or null
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            // Token signature is invalid — someone tampered with it
            System.err.println("Invalid JWT signature: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            // Token format is wrong
            System.err.println("Malformed JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            // Token has expired — user needs to log in again
            System.err.println("Expired JWT token: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            // Token uses an unsupported algorithm
            System.err.println("Unsupported JWT token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Token is empty or null
            System.err.println("JWT claims string is empty: " + ex.getMessage());
        }
        return false;
    }
}
