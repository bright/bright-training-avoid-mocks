package com.example.training.signup.repository;

import com.example.training.signup.model.User;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends DatastoreRepository<User, String> {

    /**
     * Finds a user by their email address.
     * 
     * @param email The email address
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     * 
     * @param email The email address to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
