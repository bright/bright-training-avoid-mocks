package com.example.training.signup.repository;

import com.example.training.signup.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the UserRepository interface.
 * This is a simple implementation for demonstration purposes.
 */
public class InMemoryUserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    /**
     * Saves a user to the repository.
     * 
     * @param user The user to save
     * @return The saved user
     */
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Finds a user by their ID.
     * 
     * @param id The user ID
     * @return An Optional containing the user if found, or empty if not found
     */
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Finds a user by their email address.
     * 
     * @param email The email address
     * @return An Optional containing the user if found, or empty if not found
     */
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    /**
     * Checks if a user with the given email already exists.
     * 
     * @param email The email address to check
     * @return true if a user with the email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    /**
     * Deletes a user from the repository.
     * 
     * @param user The user to delete
     */
    public void delete(User user) {
        users.remove(user.getId());
    }

    /**
     * Returns all users in the repository.
     * 
     * @return An Iterable of all users
     */
    public Iterable<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Clears all users from the repository.
     * This method is useful for testing.
     */
    public void clear() {
        users.clear();
    }
}
