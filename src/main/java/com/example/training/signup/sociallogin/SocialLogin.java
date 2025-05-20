package com.example.training.signup.sociallogin;

import com.example.training.signup.model.AuthUser;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface for social login providers.
 */
public interface SocialLogin {

    /**
     * Gets the authentication URL for the social login provider.
     * 
     * @param request The HTTP request
     * @param state A state parameter for OAuth flow
     * @return The authentication URL
     */
    String getAuthURL(HttpServletRequest request, String state);

    /**
     * Gets an authenticated user from an authorization code.
     * 
     * @param code The authorization code
     * @param request The HTTP request
     * @return The authenticated user
     */
    AuthUser getAuthUser(String code, HttpServletRequest request);

    /**
     * Gets an authenticated user from an access token.
     * 
     * @param accessToken The access token
     * @return The authenticated user
     */
    AuthUser getAuthUserByAccessToken(String accessToken);

    /**
     * Validates an authorization code.
     * 
     * @param code The authorization code to validate
     * @return true if the code is valid, false otherwise
     */
    boolean validateAuthCode(String code);
}
