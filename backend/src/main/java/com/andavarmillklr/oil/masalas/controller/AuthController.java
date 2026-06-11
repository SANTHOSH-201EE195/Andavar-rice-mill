package com.andavarmillklr.oil.masalas.controller;

import com.andavarmillklr.oil.masalas.dto.AuthResponse;
import com.andavarmillklr.oil.masalas.dto.LoginRequest;
import com.andavarmillklr.oil.masalas.dto.RegisterRequest;
import com.andavarmillklr.oil.masalas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - Exposes REST API endpoints for user authentication.
 *
 * All endpoints here start with "/api/auth".
 * These endpoints are PUBLIC (no token required), as configured in SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint: POST /api/auth/register
     * Register a new user.
     *
     * @Valid ensures the RegisterRequest meets our validation rules (e.g., password length).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            // Return 400 Bad Request if registration fails (e.g., mobile already exists)
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint: POST /api/auth/login
     * Login an existing user and get a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            // Return 401 Unauthorized if login fails
            return ResponseEntity.status(401).build();
        }
    }
}
