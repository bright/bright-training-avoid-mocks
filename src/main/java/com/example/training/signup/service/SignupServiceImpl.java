package com.example.training.signup.service;

import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SignupResponse;
import com.example.training.signup.model.User;
import com.example.training.signup.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SignupService interface.
 * 
 * PROBLEM: This implementation directly depends on HttpServletRequest and HttpServletResponse,
 * which makes it tightly coupled to the web layer and difficult to test.
 */
@Service
public class SignupServiceImpl implements SignupService {
    
    private static final Logger logger = LoggerFactory.getLogger(SignupServiceImpl.class);
    
    private final UserRepository userRepository;
    
    @Autowired
    public SignupServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public SignupResponse processSignup(HttpServletRequest request, HttpServletResponse response) {
        // Extract signup data from the request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(request.getParameter("email"));
        signupRequest.setFirstName(request.getParameter("firstName"));
        signupRequest.setLastName(request.getParameter("lastName"));
        signupRequest.setCompanyName(request.getParameter("companyName"));
        signupRequest.setPassword(request.getParameter("password"));
        signupRequest.setConfirmPassword(request.getParameter("confirmPassword"));
        signupRequest.setCountryCode(request.getParameter("countryCode"));
        signupRequest.setPhoneNumber(request.getParameter("phoneNumber"));
        
        return processSignup(signupRequest, request, response);
    }
    
    @Override
    public SignupResponse processSignup(SignupRequest signupRequest, HttpServletRequest request, HttpServletResponse response) {
        // Validate the signup request
        if (!validateSignupRequest(signupRequest)) {
            return SignupResponse.failure("Invalid signup request");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return SignupResponse.failure("User with this email already exists");
        }
        
        // Create and save the user
        User user = createUserFromRequest(signupRequest, request);
        userRepository.save(user);
        
        // Set user in session
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        
        // Set a cookie in the response
        response.addCookie(createUserCookie(user));
        
        // Return success response
        return SignupResponse.success(user.getId(), "/dashboard");
    }
    
    @Override
    public boolean validateSignupRequest(SignupRequest signupRequest) {
        // Basic validation
        if (signupRequest.getEmail() == null || signupRequest.getEmail().isEmpty()) {
            return false;
        }
        
        if (signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty()) {
            return false;
        }
        
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            return false;
        }
        
        return true;
    }
    
    private User createUserFromRequest(SignupRequest signupRequest, HttpServletRequest request) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setCompanyName(signupRequest.getCompanyName());
        user.setPassword(signupRequest.getPassword()); // In a real app, this would be hashed
        user.setIpAddress(request.getRemoteAddr());
        user.setCountryCode(signupRequest.getCountryCode());
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        return user;
    }
    
    private jakarta.servlet.http.Cookie createUserCookie(User user) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("user_email", user.getEmail());
        cookie.setMaxAge(3600); // 1 hour
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}