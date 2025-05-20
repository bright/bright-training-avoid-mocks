package com.example.training.signup.service;

import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service interface for handling user signup operations.
 * 
 * PROBLEM: This interface directly depends on HttpServletRequest and HttpServletResponse,
 * which makes it tightly coupled to the web layer and difficult to test.
 */
public interface SignupService {

    /**
     * Processes a signup request.
     * 
     * @param request The HTTP request containing signup information
     * @param response The HTTP response
     * @return A SignupResponse object with the result of the signup operation
     */
    SignupResponse processSignup(HttpServletRequest request, HttpServletResponse response);

    /**
     * Processes a signup request from the provided SignupRequest object.
     * 
     * @param signupRequest The signup request data
     * @param request The HTTP request
     * @param response The HTTP response
     * @return A SignupResponse object with the result of the signup operation
     */
    SignupResponse processSignup(SignupRequest signupRequest, HttpServletRequest request, HttpServletResponse response);

    /**
     * Validates a signup request.
     * 
     * @param signupRequest The signup request to validate
     * @return true if the request is valid, false otherwise
     */
    boolean validateSignupRequest(SignupRequest signupRequest);
}
