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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for SignupService.
 * 
 * PROBLEM: This test class demonstrates the abuse of mocks, with excessive mocking
 * and verification of implementation details rather than behavior.
 */
@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SignupHelper signupHelper;

    @Mock
    private GoogleAuth googleAuth;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private SignupService signupService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private SignupRequest validSignupRequest;

    private AuthUser authUser;
    private SessionInfo sessionInfo;
    private Map<String, Object> additionalInfo;
    private SocialLoginSignup socialLoginSignup;

    @BeforeEach
    void setUp() {
        validSignupRequest = new SignupRequest();
        validSignupRequest.setEmail("test@example.com");
        validSignupRequest.setFirstName("Test");
        validSignupRequest.setLastName("User");
        validSignupRequest.setCompanyName("Test Company");
        validSignupRequest.setPassword("password123");
        validSignupRequest.setConfirmPassword("password123");
        validSignupRequest.setCountryCode("US");
        validSignupRequest.setPhoneNumber("1234567890");

        authUser = new AuthUser();
        authUser.setEmail("social@example.com");
        authUser.setFirstName("Social");
        authUser.setLastName("User");
        authUser.setAccessToken("access_token_123");
        authUser.setAuthType("GOOGLE");

        sessionInfo = new SessionInfo();

        additionalInfo = new HashMap<>();
        additionalInfo.put("signupType", "free");
        additionalInfo.put("authType", AuthType.GOOGLE);

        socialLoginSignup = new SocialLoginSignup();
        socialLoginSignup.setAccessToken("access_token_123");
        socialLoginSignup.setEmail("social@example.com");
        socialLoginSignup.setFirstName("Social");
        socialLoginSignup.setLastName("User");
    }

    @Test
    @DisplayName("processSignup should return success response when signup is valid")
    void processSignup_WithValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SignupResponse response = signupService.processSignup(validSignupRequest, request, this.response);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getUserId());
        assertEquals("/dashboard", response.getRedirectUrl());

        // Verify interactions with mocks
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(userCaptor.capture());
        verify(session).setAttribute(eq("userId"), anyString());
        verify(this.response).addCookie(cookieCaptor.capture());

        // Verify captured arguments
        User savedUser = userCaptor.getValue();
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test", savedUser.getFirstName());
        assertEquals("127.0.0.1", savedUser.getIpAddress());

        Cookie cookie = cookieCaptor.getValue();
        assertEquals("user_email", cookie.getName());
        assertEquals("test@example.com", cookie.getValue());
        assertEquals(3600, cookie.getMaxAge());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    @DisplayName("processSignup should return failure response when user already exists")
    void processSignup_WithExistingUser_ShouldReturnFailureResponse() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        SignupResponse response = signupService.processSignup(validSignupRequest, request, this.response);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("User with this email already exists", response.getMessage());

        // Verify interactions with mocks
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(session, never()).setAttribute(anyString(), any());
        verify(this.response, never()).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("processSignup should return failure response when passwords don't match")
    void processSignup_WithNonMatchingPasswords_ShouldReturnFailureResponse() {
        // Arrange
        validSignupRequest.setConfirmPassword("differentPassword");

        // Act
        SignupResponse response = signupService.processSignup(validSignupRequest, request, this.response);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Invalid signup request", response.getMessage());

        // Verify interactions with mocks
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(session, never()).setAttribute(anyString(), any());
        verify(this.response, never()).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("processSignup with HttpServletRequest should extract parameters and process signup")
    void processSignup_WithHttpServletRequest_ShouldExtractParametersAndProcessSignup() {
        // Arrange
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("firstName")).thenReturn("Test");
        when(request.getParameter("lastName")).thenReturn("User");
        when(request.getParameter("companyName")).thenReturn("Test Company");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("countryCode")).thenReturn("US");
        when(request.getParameter("phoneNumber")).thenReturn("1234567890");
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SignupResponse response = signupService.processSignup(request, this.response);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getUserId());
        assertEquals("/dashboard", response.getRedirectUrl());

        // Verify interactions with mocks
        verify(request).getParameter("email");
        verify(request).getParameter("firstName");
        verify(request).getParameter("lastName");
        verify(request).getParameter("companyName");
        verify(request).getParameter("password");
        verify(request).getParameter("confirmPassword");
        verify(request).getParameter("countryCode");
        verify(request).getParameter("phoneNumber");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(userCaptor.capture());
        verify(session).setAttribute(eq("userId"), anyString());
        verify(this.response).addCookie(cookieCaptor.capture());

        // Verify captured arguments
        User savedUser = userCaptor.getValue();
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test", savedUser.getFirstName());
    }

    @Test
    @DisplayName("validateSignupRequest should return true for valid request")
    void validateSignupRequest_WithValidRequest_ShouldReturnTrue() {
        // Act
        boolean isValid = signupService.validateSignupRequest(validSignupRequest);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("validateSignupRequest should return false when email is null")
    void validateSignupRequest_WithNullEmail_ShouldReturnFalse() {
        // Arrange
        validSignupRequest.setEmail(null);

        // Act
        boolean isValid = signupService.validateSignupRequest(validSignupRequest);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("validateSignupRequest should return false when password is null")
    void validateSignupRequest_WithNullPassword_ShouldReturnFalse() {
        // Arrange
        validSignupRequest.setPassword(null);

        // Act
        boolean isValid = signupService.validateSignupRequest(validSignupRequest);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("validateSignupRequest should return false when passwords don't match")
    void validateSignupRequest_WithNonMatchingPasswords_ShouldReturnFalse() {
        // Arrange
        validSignupRequest.setConfirmPassword("differentPassword");

        // Act
        boolean isValid = signupService.validateSignupRequest(validSignupRequest);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("processSocialLoginReq should process social login and return dashboard URL")
    void processSocialLoginReq_ShouldProcessSocialLoginAndReturnDashboardURL() {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("sessionInfo")).thenReturn(sessionInfo);
        doNothing().when(signupHelper).processSocialLoginReq(
                eq(request), eq(authUser), eq(sessionInfo), eq(session), eq(additionalInfo));
        sessionInfo.setAuthorized(true);

        // Act
        String result = signupService.processSocialLoginReq(authUser, request, additionalInfo, response);

        // Assert
        assertEquals("/dashboard", result);

        // Verify interactions with mocks
        verify(request).getSession(false);
        verify(session).getAttribute("sessionInfo");
        verify(signupHelper).processSocialLoginReq(request, authUser, sessionInfo, session, additionalInfo);
    }

    @Test
    @DisplayName("processSocialLoginReq should return dashboard URL when session does not exist")
    void processSocialLoginReq_WhenSessionDoesNotExist_ShouldReturnDashboardURL() {
        // Arrange
        when(request.getSession(false)).thenReturn(null);

        // Act
        String result = signupService.processSocialLoginReq(authUser, request, additionalInfo, response);

        // Assert
        assertEquals("/dashboard", result);

        // Verify interactions with mocks
        verify(request).getSession(false);
        verify(signupHelper, never()).processSocialLoginReq(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("processSocialLoginFreeSignup should process free signup and return redirect URL")
    void processSocialLoginFreeSignup_ShouldProcessFreeSignupAndReturnRedirectURL() {
        // Arrange
        User user = new User();
        user.setEmail("social@example.com");
        user.setFirstName("Social");
        user.setLastName("User");
        sessionInfo.setUser(user);

        // Create a spy of the signupService to mock the processSignup method
        SignupService spySignupService = spy(signupService);

        // Mock the processSignup method to return a successful response
        SignupResponse successResponse = SignupResponse.success("user123", "/dashboard");
        doReturn(successResponse).when(spySignupService).processSignup(any(SignupRequest.class), eq(request), eq(response));

        // Act
        String result = spySignupService.processSocialLoginFreeSignup(request, response, sessionInfo);

        // Assert
        assertEquals("redirect:/dashboard", result);

        // Verify interactions with mocks
        verify(request).setAttribute("isSocialLoginFlow", true);
        verify(spySignupService).processSignup(any(SignupRequest.class), eq(request), eq(response));
    }

    @Test
    @DisplayName("processSocialLoginSignup should process social login signup and return success response")
    void processSocialLoginSignup_ShouldProcessSocialLoginSignupAndReturnSuccessResponse() {
        // Arrange
        when(signupHelper.getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup)).thenReturn(authUser);
        when(signupHelper.getSignupDetails(authUser, socialLoginSignup)).thenReturn(validSignupRequest);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId("user123");
            return savedUser;
        });

        // Act
        ResponseEntity<SignupResponse> responseEntity = signupService.processSocialLoginSignup("GOOGLE", socialLoginSignup);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        SignupResponse signupResponse = responseEntity.getBody();
        assertNotNull(signupResponse);
        assertTrue(signupResponse.isSuccess());
        assertEquals("user123", signupResponse.getUserId());
        assertEquals("/dashboard", signupResponse.getRedirectUrl());

        // Verify interactions with mocks
        verify(signupHelper).getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup);
        verify(signupHelper).getSignupDetails(authUser, socialLoginSignup);
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("processSocialLoginSignup should return failure response when user already exists")
    void processSocialLoginSignup_WhenUserAlreadyExists_ShouldReturnFailureResponse() {
        // Arrange
        when(signupHelper.getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup)).thenReturn(authUser);
        when(signupHelper.getSignupDetails(authUser, socialLoginSignup)).thenReturn(validSignupRequest);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        ResponseEntity<SignupResponse> responseEntity = signupService.processSocialLoginSignup("GOOGLE", socialLoginSignup);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        SignupResponse signupResponse = responseEntity.getBody();
        assertNotNull(signupResponse);
        assertFalse(signupResponse.isSuccess());
        assertEquals("User with this email already exists", signupResponse.getMessage());

        // Verify interactions with mocks
        verify(signupHelper).getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup);
        verify(signupHelper).getSignupDetails(authUser, socialLoginSignup);
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("processSocialLoginSignup should return error response when auth user is null")
    void processSocialLoginSignup_WhenAuthUserIsNull_ShouldReturnErrorResponse() {
        // Arrange
        when(signupHelper.getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup)).thenReturn(null);

        // Act
        ResponseEntity<SignupResponse> responseEntity = signupService.processSocialLoginSignup("GOOGLE", socialLoginSignup);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        SignupResponse signupResponse = responseEntity.getBody();
        assertNotNull(signupResponse);
        assertFalse(signupResponse.isSuccess());
        assertEquals("Auth User is null or doesn't have an email address", signupResponse.getMessage());

        // Verify interactions with mocks
        verify(signupHelper).getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup);
        verify(signupHelper, never()).getSignupDetails(any(), any());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
