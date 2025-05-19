package com.example.training.signup.repository;

import com.example.training.signup.model.User;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
public interface UserRepository {
    
    /**
     * Saves a user to the repository.
     * 
     * @param user The user to save
     * @return The saved user with any generated IDs or fields
     */
    User save(User user);
    
    /**
     * Finds a user by their ID.
     * 
     * @param id The user ID
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findById(String id);
    
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
    
    /**
     * Deletes a user from the repository.
     * 
     * @param user The user to delete
     */
    void delete(User user);
}