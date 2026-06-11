package com.andavarmillklr.oil.masalas.security;

import com.andavarmillklr.oil.masalas.model.User;
import com.andavarmillklr.oil.masalas.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService - Connects Spring Security to our database.
 *
 * Spring Security needs a way to look up users when they try to log in.
 * This class implements Spring's UserDetailsService interface to load
 * our User entity from the database and convert it into a format that
 * Spring Security understands (UserDetails).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /** Constructor for dependency injection */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method is called by Spring Security when a user attempts to log in.
     * We use the mobile number as the "username".
     *
     * @param username The user's mobile number
     * @return UserDetails object containing credentials and authorities
     * @throws UsernameNotFoundException if the mobile number is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user by their mobile number
        User user = userRepository.findByMobile(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile: " + username));

        // Convert our User entity into Spring's UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getMobile(),                                     // The username
                user.getPassword(),                                   // The hashed password
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())) // The user's role
        );
    }
}
