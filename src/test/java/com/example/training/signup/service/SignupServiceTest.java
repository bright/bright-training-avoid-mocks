package com.example.training.signup.service;

import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SignupResponse;
import com.example.training.signup.model.User;
import com.example.training.signup.repository.UserRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
}
