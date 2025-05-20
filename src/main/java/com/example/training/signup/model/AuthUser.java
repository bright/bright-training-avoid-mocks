package com.example.training.signup.model;

/**
 * Represents an authenticated user from a social login provider.
 */
public class AuthUser {
    private String email;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String authType;
    
    public AuthUser() {
    }
    
    public AuthUser(String email, String firstName, String lastName, String accessToken, String authType) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accessToken = accessToken;
        this.authType = authType;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAuthType() {
        return authType;
    }
    
    public void setAuthType(String authType) {
        this.authType = authType;
    }
}