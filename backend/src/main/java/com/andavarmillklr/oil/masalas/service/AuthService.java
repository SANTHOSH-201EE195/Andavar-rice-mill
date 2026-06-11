package com.andavarmillklr.oil.masalas.service;

import com.andavarmillklr.oil.masalas.dto.AuthResponse;
import com.andavarmillklr.oil.masalas.dto.LoginRequest;
import com.andavarmillklr.oil.masalas.dto.RegisterRequest;
import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.repository.UserRepository;
import com.andavarmillklr.oil.masalas.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService - Handles user registration and login logic.
 *
 * This service is responsible for:
 * 1. Registering new users (hashing passwords, saving to DB)
 * 2. Authenticating users (checking mobile/password)
 * 3. Generating JWT tokens
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register a new user.
     *
     * @param request The registration details
     * @return AuthResponse containing JWT token and user info
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Check if mobile number is already taken
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new RuntimeException("Mobile number is already registered.");
        }

        // 2. Create the user entity
        User user = new User();
        user.setMobile(request.getMobile());
        // ALWAYS hash the password before saving!
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        
        // Make the specific mobile number the admin
        if ("7010903976".equals(request.getMobile())) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }

        // 3. Save to database
        user = userRepository.save(user);

        // 4. Generate JWT token for the new user
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());

        // 5. Return the response
        return new AuthResponse(token, user.getId(), user.getMobile(), user.getFullName(), user.getEmail(), user.getRole());
    }

    /**
     * Login an existing user.
     *
     * @param request The login details (mobile & password)
     * @return AuthResponse containing JWT token and user info
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Authenticate using Spring Security
        // This will call our CustomUserDetailsService to load the user and check the password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMobile(), request.getPassword())
        );

        // 2. If authentication succeeds, find the user in our database to get their details
        User user = userRepository.findByMobile(request.getMobile())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate a new JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());

        // 4. Return the response
        return new AuthResponse(token, user.getId(), user.getMobile(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
