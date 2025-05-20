package com.example.training.signup.model;

/**
 * Represents session information for a user.
 */
public class SessionInfo {
    private User user;
    private boolean authorized;
    private String authType;
    
    public SessionInfo() {
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean isAuthorized() {
        return authorized;
    }
    
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
    
    public String getAuthType() {
        return authType;
    }
    
    public void setAuthType(String authType) {
        this.authType = authType;
    }
}