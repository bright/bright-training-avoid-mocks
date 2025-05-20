package com.example.training.signup.service;

import com.example.training.signup.helper.SignupHelper;
import com.example.training.signup.model.AuthUser;
import com.example.training.signup.model.SessionInfo;
import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SignupResponse;
import com.example.training.signup.model.SocialLoginSignup;
import com.example.training.signup.model.User;
import com.example.training.signup.repository.UserRepository;
import com.example.training.signup.sociallogin.AuthType;
import com.example.training.signup.sociallogin.GoogleAuth;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for handling user signup operations.
 * 
 * This service now incorporates social login functionality using GoogleAuth
 * and SignupHelper for processing different types of signups.
 */
@Service
public class SignupService {

    private static final Logger logger = LoggerFactory.getLogger(SignupService.class);
    private static final String SESSION_INFO = "sessionInfo";
    private static final String IS_SOCIAL_LOGIN_FLOW = "isSocialLoginFlow";
    private static final String SIGNUP_TYPE = "signupType";

    private final UserRepository userRepository;
    private final SignupHelper signupHelper;
    private final GoogleAuth googleAuth;

    @Autowired
    public SignupService(UserRepository userRepository, SignupHelper signupHelper, GoogleAuth googleAuth) {
        this.userRepository = userRepository;
        this.signupHelper = signupHelper;
        this.googleAuth = googleAuth;
    }

    /**
     * Processes a signup request.
     * 
     * @param request The HTTP request containing signup information
     * @param response The HTTP response
     * @return A SignupResponse object with the result of the signup operation
     */
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

    /**
     * Processes a signup request from the provided SignupRequest object.
     * 
     * @param signupRequest The signup request data
     * @param request The HTTP request
     * @param response The HTTP response
     * @return A SignupResponse object with the result of the signup operation
     */
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

    /**
     * Processes a social login request.
     * 
     * @param authUser The authenticated user
     * @param request The HTTP request
     * @param additionalInfo Additional information for the request
     * @param response The HTTP response
     * @return The URL to redirect to
     */
    public String processSocialLoginReq(
            AuthUser authUser,
            HttpServletRequest request,
            Map<String, Object> additionalInfo,
            HttpServletResponse response) {
        try {
            Optional<HttpSession> session = Optional.ofNullable(request.getSession(false));
            if (!session.isPresent()) {
                throw new IllegalStateException("Session does not exist");
            }

            SessionInfo sessionInfo = (SessionInfo) session.get().getAttribute(SESSION_INFO);
            if (sessionInfo == null) {
                sessionInfo = new SessionInfo();
                session.get().setAttribute(SESSION_INFO, sessionInfo);
            }

            signupHelper.processSocialLoginReq(request, authUser, sessionInfo, session.get(), additionalInfo);

            if (sessionInfo.isAuthorized()) {
                String signupType = (String) additionalInfo.get(SIGNUP_TYPE);
                if ("free".equals(signupType)) {
                    return processSocialLoginFreeSignup(request, response, sessionInfo);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in processSocialLoginReq: ", e);
        }
        return "/dashboard";
    }

    /**
     * Processes a free signup from a social login.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param sessionInfo The session information
     * @return The URL to redirect to
     */
    public String processSocialLoginFreeSignup(
            HttpServletRequest request,
            HttpServletResponse response,
            SessionInfo sessionInfo) {
        // Flag to indicate signup step to not expect captchaToken
        request.setAttribute(IS_SOCIAL_LOGIN_FLOW, true);

        SignupRequest signupRequest = new SignupRequest();
        User user = sessionInfo.getUser();
        signupRequest.setEmail(user.getEmail());
        signupRequest.setFirstName(user.getFirstName());
        signupRequest.setLastName(user.getLastName());
        signupRequest.setCompanyName(user.getCompanyName());

        SignupResponse signupResponse = processSignup(signupRequest, request, response);

        if (signupResponse.isSuccess()) {
            return "redirect:" + signupResponse.getRedirectUrl();
        }
        throw new IllegalStateException("Signup failure");
    }

    /**
     * Processes a social login signup.
     * 
     * @param authType The authentication type
     * @param socialLoginSignup The social login signup request
     * @return A ResponseEntity with the result of the signup operation
     */
    public ResponseEntity<SignupResponse> processSocialLoginSignup(
            String authType,
            SocialLoginSignup socialLoginSignup) {
        try {
            AuthType authTypeEnum = AuthType.valueOf(authType.toUpperCase());

            AuthUser authUser = signupHelper.getUserByAccessToken(authTypeEnum, socialLoginSignup);

            if (authUser == null || authUser.getEmail() == null || authUser.getEmail().isEmpty()) {
                throw new IllegalStateException("Auth User is null or doesn't have an email address");
            }

            logger.info("Authenticated user email: {}", authUser.getEmail());

            SignupRequest signupRequest = signupHelper.getSignupDetails(authUser, socialLoginSignup);

            // Create and save the user
            if (!userRepository.existsByEmail(signupRequest.getEmail())) {
                User user = new User();
                user.setEmail(signupRequest.getEmail());
                user.setFirstName(signupRequest.getFirstName());
                user.setLastName(signupRequest.getLastName());
                user.setCompanyName(signupRequest.getCompanyName());
                userRepository.save(user);

                SignupResponse signupResponse = SignupResponse.success(user.getId(), "/dashboard");
                return new ResponseEntity<>(signupResponse, HttpStatus.OK);
            } else {
                SignupResponse signupResponse = SignupResponse.failure("User with this email already exists");
                return new ResponseEntity<>(signupResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error in processSocialLoginSignup: ", e);
            SignupResponse signupResponse = SignupResponse.failure(e.getMessage());
            return new ResponseEntity<>(signupResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Validates a signup request.
     * 
     * @param signupRequest The signup request to validate
     * @return true if the request is valid, false otherwise
     */
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

    private Cookie createUserCookie(User user) {
        Cookie cookie = new Cookie("user_email", user.getEmail());
        cookie.setMaxAge(3600); // 1 hour
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
