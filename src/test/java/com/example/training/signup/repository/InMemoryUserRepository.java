package com.example.training.signup.repository;

import com.example.training.signup.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the UserRepository interface.
 * This is a simple implementation for demonstration purposes.
 */
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }
    
    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }
    
    /**
     * Clears all users from the repository.
     * This method is useful for testing.
     */
    public void clear() {
        users.clear();
    }
}