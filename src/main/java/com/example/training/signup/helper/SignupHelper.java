package com.example.training.signup.helper;

import com.example.training.signup.model.AuthUser;
import com.example.training.signup.model.SessionInfo;
import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SocialLoginSignup;
import com.example.training.signup.model.User;
import com.example.training.signup.repository.UserRepository;
import com.example.training.signup.sociallogin.AuthType;
import com.example.training.signup.sociallogin.GoogleAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Helper class for signup operations.
 */
@Component
public class SignupHelper {

    private static final Logger logger = LoggerFactory.getLogger(SignupHelper.class);
    private static final String SESSION_INFO = "sessionInfo";
    private static final String SOCIAL_LOGIN_TOKEN = "socialLoginToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    
    private final UserRepository userRepository;
    private final GoogleAuth googleAuth;
    
    @Autowired
    public SignupHelper(UserRepository userRepository, GoogleAuth googleAuth) {
        this.userRepository = userRepository;
        this.googleAuth = googleAuth;
    }
    
    /**
     * Processes a social login request.
     * 
     * @param request The HTTP request
     * @param authUser The authenticated user
     * @param sessionInfo The session information
     * @param session The HTTP session
     * @param additionalInfo Additional information for the request
     */
    public void processSocialLoginReq(
            HttpServletRequest request,
            AuthUser authUser,
            SessionInfo sessionInfo,
            HttpSession session,
            Map<String, Object> additionalInfo) {
        
        logger.info("Processing social login for email: {}", authUser.getEmail());
        
        // Set user information in session info
        User user = new User();
        user.setEmail(authUser.getEmail());
        user.setFirstName(authUser.getFirstName());
        user.setLastName(authUser.getLastName());
        sessionInfo.setUser(user);
        
        // Set auth type
        sessionInfo.setAuthType(authUser.getAuthType());
        
        // Check if user already exists
        sessionInfo.setAuthorized(!userRepository.existsByEmail(authUser.getEmail()));
        
        // Store tokens in session
        if ("GOOGLE".equals(authUser.getAuthType())) {
            session.setAttribute(REFRESH_TOKEN, googleAuth.getRefreshToken());
        }
        
        session.setAttribute(SOCIAL_LOGIN_TOKEN, authUser.getAccessToken());
        session.setAttribute(SESSION_INFO, sessionInfo);
    }

    /**
     * Gets an authenticated user by access token.
     * 
     * @param authType The authentication type
     * @param socialLoginSignup The social login signup request
     * @return The authenticated user
     */
    public AuthUser getUserByAccessToken(AuthType authType, SocialLoginSignup socialLoginSignup) {
        try {
            if (authType == AuthType.GOOGLE) {
                return googleAuth.getAuthUserByAccessToken(socialLoginSignup.getAccessToken());
            }
            // Handle other auth types as needed
            return null;
        } catch (Exception e) {
            logger.error("Error getting user by access token", e);
            return null;
        }
    }
    
    /**
     * Gets signup details from an authenticated user.
     * 
     * @param authUser The authenticated user
     * @param socialLoginSignup The social login signup request
     * @return A SignupRequest object with the user's information
     */
    public SignupRequest getSignupDetails(AuthUser authUser, SocialLoginSignup socialLoginSignup) {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(authUser.getEmail());
        signupRequest.setFirstName(getUserName(authUser.getFirstName(), authUser.getEmail()));
        signupRequest.setLastName(authUser.getLastName());
        signupRequest.setCompanyName(determineCompanyName(socialLoginSignup.getFirstName(), authUser.getEmail()));
        return signupRequest;
    }
    
    private String getUserName(String firstName, String email) {
        try {
            return firstName != null && !firstName.isEmpty() ? firstName : email.split("@")[0];
        } catch (Exception e) {
            logger.error("Error getting user name", e);
            return "";
        }
    }
    
    private String determineCompanyName(String firstName, String email) {
        try {
            return firstName != null && !firstName.isEmpty() ? firstName : email.split("@")[0];
        } catch (Exception e) {
            logger.error("Error determining company name", e);
            return "";
        }
    }
}