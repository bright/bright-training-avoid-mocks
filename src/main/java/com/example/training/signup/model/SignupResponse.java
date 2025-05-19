package com.example.training.signup.model;

/**
 * Represents the response to a signup request.
 * This class is used to return the result of a signup operation.
 */
public class SignupResponse {
    private boolean success;
    private String message;
    private String userId;
    private String redirectUrl;
    
    public SignupResponse() {
    }
    
    public SignupResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    /**
     * Creates a successful response.
     * 
     * @param userId The ID of the created user
     * @param redirectUrl The URL to redirect to after successful signup
     * @return A successful SignupResponse
     */
    public static SignupResponse success(String userId, String redirectUrl) {
        SignupResponse response = new SignupResponse(true, "Signup successful");
        response.setUserId(userId);
        response.setRedirectUrl(redirectUrl);
        return response;
    }
    
    /**
     * Creates a failure response.
     * 
     * @param message The error message
     * @return A failure SignupResponse
     */
    public static SignupResponse failure(String message) {
        return new SignupResponse(false, message);
    }
}