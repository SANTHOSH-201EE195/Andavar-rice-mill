package com.andavarmillklr.oil.masalas.repository;

import com.andavarmillklr.oil.masalas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * UserRepository - Database access layer for the User entity.
 *
 * Spring Data JPA automatically creates the implementation for this interface.
 * We only need to declare method signatures, and Spring generates the SQL queries.
 *
 * JpaRepository<User, Long> means:
 *   - User  = the entity type we are working with
 *   - Long  = the type of the primary key (id)
 *
 * Built-in methods we get for free:
 *   - findAll()         → Get all users
 *   - findById(id)      → Find a user by their ID
 *   - save(user)        → Insert or update a user
 *   - deleteById(id)    → Delete a user by their ID
 *   - count()           → Count total users
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their mobile number.
     * Used during login to check if the mobile number exists.
     *
     * Spring automatically generates: SELECT * FROM users WHERE mobile = ?
     *
     * @param mobile The mobile number to search for
     * @return Optional containing the User if found, or empty if not found
     */
    Optional<User> findByMobile(String mobile);

    /**
     * Check if a user with the given mobile number already exists.
     * Used during registration to prevent duplicate accounts.
     *
     * Spring automatically generates: SELECT COUNT(*) > 0 FROM users WHERE mobile = ?
     *
     * @param mobile The mobile number to check
     * @return true if a user with this mobile number exists, false otherwise
     */
    boolean existsByMobile(String mobile);
}
