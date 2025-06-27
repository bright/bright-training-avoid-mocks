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
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for SignupService.
 *
 * This test class uses @SpringBootTest to load the application context
 * and relies on LocalDatastoreTestsAutoConfiguration for real UserRepository
 * backed by local datastore instead of mocks.
 */
@SpringBootTest
@ActiveProfiles("datastore-tests")
class SignupServiceTest {

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private SignupHelper signupHelper;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    @Autowired
    private SignupService signupService;

    private SignupRequest validSignupRequest;

    private AuthUser authUser;
    private SessionInfo sessionInfo;
    private Map<String, Object> additionalInfo;
    private SocialLoginSignup socialLoginSignup;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

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

        // Create mock HTTP objects
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // Set up common request properties
        request.setRemoteAddr("127.0.0.1");
        request.setSession(session);
    }

    @Test
    @DisplayName("processSignup should return success response when signup is valid")
    void processSignup_WithValidRequest_ShouldReturnSuccessResponse() {
        // given
        when(signupHelper.validateSignupRequest(eq(validSignupRequest), eq(false))).thenReturn(true);

        // when
        SignupResponse response = signupService.processSignup(validSignupRequest, request, this.response);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getRedirectUrl()).isEqualTo("/dashboard");

        // Verify interactions with SignupHelper mock
        verify(signupHelper).validateSignupRequest(eq(validSignupRequest), eq(false));

        // Verify user was saved to repository
        Optional<User> savedUser = userRepository.findByEmail("test@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.get().getFirstName()).isEqualTo("Test");
        assertThat(savedUser.get().getLastName()).isEqualTo("User");
        assertThat(savedUser.get().getCompanyName()).isEqualTo("Test Company");
        assertThat(savedUser.get().getIpAddress()).isEqualTo("127.0.0.1");

        // Verify session attribute was set
        assertThat(session.getAttribute("userId")).isNotNull();

        // Verify cookie was added
        Cookie[] cookies = this.response.getCookies();
        assertThat(cookies).isNotNull();
        assertThat(cookies.length).isGreaterThan(0);
        Cookie cookie = cookies[0];
        assertThat(cookie.getName()).isEqualTo("user_email");
        assertThat(cookie.getValue()).isEqualTo("test@example.com");
        assertThat(cookie.getMaxAge()).isEqualTo(3600);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    @Test
    @DisplayName("processSignup should return failure response when user already exists")
    void processSignup_WithExistingUser_ShouldReturnFailureResponse() {
        // given
        when(signupHelper.validateSignupRequest(eq(validSignupRequest), eq(false))).thenReturn(true);

        // Create an existing user in the repository
        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        userRepository.save(existingUser);

        // when
        SignupResponse response = signupService.processSignup(validSignupRequest, request, this.response);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("User with this email already exists");

        // Verify interactions with SignupHelper mock
        verify(signupHelper).validateSignupRequest(eq(validSignupRequest), eq(false));

        // Verify session attribute was not set
        assertThat(session.getAttribute("userId")).isNull();

        // Verify no cookies were added
        Cookie[] cookies = this.response.getCookies();
        assertThat(cookies == null || cookies.length == 0).isTrue();
    }

    @Test
    @DisplayName("processSignup should return failure response when passwords don't match")
    void processSignup_WithNonMatchingPasswords_ShouldReturnFailureResponse() {
        // given
        validSignupRequest.setConfirmPassword("differentPassword");
        when(signupHelper.validateSignupRequest(eq(validSignupRequest), eq(false))).thenReturn(false);

        // when
        SignupResponse response = signupService.processSignup(validSignupRequest, request, this.response);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid signup request");

        // Verify interactions with SignupHelper mock
        verify(signupHelper).validateSignupRequest(eq(validSignupRequest), eq(false));

        // Verify no user was saved to repository
        assertThat(userRepository.findByEmail("test@example.com")).isEmpty();

        // Verify session attribute was not set
        assertThat(session.getAttribute("userId")).isNull();

        // Verify no cookies were added
        Cookie[] cookies = this.response.getCookies();
        assertThat(cookies == null || cookies.length == 0).isTrue();
    }


    @Test
    @DisplayName("validateSignupRequest should return true for valid request")
    void validateSignupRequest_WithValidRequest_ShouldReturnTrue() {
        // Arrange
        when(signupHelper.validateSignupRequest(validSignupRequest)).thenReturn(true);

        // Act
        boolean isValid = signupHelper.validateSignupRequest(validSignupRequest);

        // Assert
        assertTrue(isValid);
        verify(signupHelper).validateSignupRequest(validSignupRequest);
    }

    @Test
    @DisplayName("validateSignupRequest should return false when email is null")
    void validateSignupRequest_WithNullEmail_ShouldReturnFalse() {
        // Arrange
        validSignupRequest.setEmail(null);
        when(signupHelper.validateSignupRequest(validSignupRequest)).thenReturn(false);

        // Act
        boolean isValid = signupHelper.validateSignupRequest(validSignupRequest);

        // Assert
        assertFalse(isValid);
        verify(signupHelper).validateSignupRequest(validSignupRequest);
    }

    @Test
    @DisplayName("validateSignupRequest should return false when password is null")
    void validateSignupRequest_WithNullPassword_ShouldReturnFalse() {
        // Arrange
        validSignupRequest.setPassword(null);
        when(signupHelper.validateSignupRequest(validSignupRequest)).thenReturn(false);

        // Act
        boolean isValid = signupHelper.validateSignupRequest(validSignupRequest);

        // Assert
        assertFalse(isValid);
        verify(signupHelper).validateSignupRequest(validSignupRequest);
    }

    @Test
    @DisplayName("validateSignupRequest should return false when passwords don't match")
    void validateSignupRequest_WithNonMatchingPasswords_ShouldReturnFalse() {
        // Arrange
        validSignupRequest.setConfirmPassword("differentPassword");
        when(signupHelper.validateSignupRequest(validSignupRequest)).thenReturn(false);

        // Act
        boolean isValid = signupHelper.validateSignupRequest(validSignupRequest);

        // Assert
        assertFalse(isValid);
        verify(signupHelper).validateSignupRequest(validSignupRequest);
    }

    @Test
    @DisplayName("processSocialLoginReq should process social login and return dashboard URL")
    void processSocialLoginReq_ShouldProcessSocialLoginAndReturnDashboardURL() {
        // Arrange
        session.setAttribute("sessionInfo", sessionInfo);
        doNothing().when(signupHelper).processSocialLoginReq(
                eq(request), eq(authUser), eq(sessionInfo), eq(session), eq(additionalInfo));
        sessionInfo.setAuthorized(true);

        // Act
        String result = signupService.processSocialLoginReq(authUser, request, additionalInfo, response);

        // Assert
        assertEquals("/dashboard", result);

        // Verify interactions with mocks
        verify(signupHelper).processSocialLoginReq(request, authUser, sessionInfo, session, additionalInfo);
    }

    @Test
    @DisplayName("processSocialLoginReq should return dashboard URL when session does not exist")
    void processSocialLoginReq_WhenSessionDoesNotExist_ShouldReturnDashboardURL() {
        // Arrange
        // Create a new request without a session
        MockHttpServletRequest requestWithoutSession = new MockHttpServletRequest();

        // Act
        String result = signupService.processSocialLoginReq(authUser, requestWithoutSession, additionalInfo, response);

        // Assert
        assertEquals("/dashboard", result);

        // Verify interactions with mocks
        verify(signupHelper, never()).processSocialLoginReq(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("processSocialLoginFreeSignup should process free signup and return redirect URL")
    void processSocialLoginFreeSignup_ShouldProcessFreeSignupAndReturnRedirectURL() {
        // given
        User user = new User();
        user.setEmail("social@example.com");
        user.setFirstName("Social");
        user.setLastName("User");
        user.setCompanyName("Social Company");
        sessionInfo.setUser(user);

        // Mock signupHelper.validateSignupRequest to return true for any SignupRequest with isSocialLogin=true
        when(signupHelper.validateSignupRequest(any(SignupRequest.class), eq(true))).thenReturn(true);

        // when
        String result = signupService.processSocialLoginFreeSignup(request, response, sessionInfo);

        // then
        assertThat(result).isEqualTo("redirect:/dashboard");

        // Verify request attribute was set
        assertThat(request.getAttribute("isSocialLoginFlow")).isEqualTo(true);

        // Verify user was saved to repository
        Optional<User> savedUser = userRepository.findByEmail("social@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("social@example.com");
        assertThat(savedUser.get().getFirstName()).isEqualTo("Social");
        assertThat(savedUser.get().getLastName()).isEqualTo("User");
        assertThat(savedUser.get().getCompanyName()).isEqualTo("Social Company");

        // Verify session attribute was set
        assertThat(session.getAttribute("userId")).isNotNull();

        // Verify cookie was added
        Cookie[] cookies = response.getCookies();
        assertThat(cookies).isNotNull();
        assertThat(cookies.length).isGreaterThan(0);
        Cookie cookie = cookies[0];
        assertThat(cookie.getName()).isEqualTo("user_email");
        assertThat(cookie.getValue()).isEqualTo("social@example.com");
    }

    @Test
    @DisplayName("processSocialLoginSignup should process social login signup and return success response")
    void processSocialLoginSignup_ShouldProcessSocialLoginSignupAndReturnSuccessResponse() {
        // given
        SignupRequest socialSignupRequest = new SignupRequest();
        socialSignupRequest.setEmail("social@example.com");
        socialSignupRequest.setFirstName("Social");
        socialSignupRequest.setLastName("User");
        socialSignupRequest.setCompanyName("Social Company");
        socialSignupRequest.setPassword("password123");
        socialSignupRequest.setConfirmPassword("password123");
        socialSignupRequest.setCountryCode("US");
        socialSignupRequest.setPhoneNumber("1234567890");

        when(signupHelper.getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup)).thenReturn(authUser);
        when(signupHelper.getSignupDetails(authUser, socialLoginSignup)).thenReturn(socialSignupRequest);

        // when
        ResponseEntity<SignupResponse> responseEntity = signupService.processSocialLoginSignup("GOOGLE", socialLoginSignup);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SignupResponse signupResponse = responseEntity.getBody();
        assertThat(signupResponse).isNotNull();
        assertThat(signupResponse.isSuccess()).isTrue();
        assertThat(signupResponse.getUserId()).isNotNull();
        assertThat(signupResponse.getRedirectUrl()).isEqualTo("/dashboard");

        // Verify interactions with SignupHelper mocks
        verify(signupHelper).getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup);
        verify(signupHelper).getSignupDetails(authUser, socialLoginSignup);

        // Verify user was saved to repository
        Optional<User> savedUser = userRepository.findByEmail("social@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("social@example.com");
        assertThat(savedUser.get().getFirstName()).isEqualTo("Social");
        assertThat(savedUser.get().getLastName()).isEqualTo("User");
    }

    @Test
    @DisplayName("processSocialLoginSignup should return failure response when user already exists")
    void processSocialLoginSignup_WhenUserAlreadyExists_ShouldReturnFailureResponse() {
        // given
        SignupRequest socialSignupRequest = new SignupRequest();
        socialSignupRequest.setEmail("social@example.com");
        socialSignupRequest.setFirstName("Social");
        socialSignupRequest.setLastName("User");
        socialSignupRequest.setCompanyName("Social Company");
        socialSignupRequest.setPassword("password123");
        socialSignupRequest.setConfirmPassword("password123");
        socialSignupRequest.setCountryCode("US");
        socialSignupRequest.setPhoneNumber("1234567890");

        when(signupHelper.getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup)).thenReturn(authUser);
        when(signupHelper.getSignupDetails(authUser, socialLoginSignup)).thenReturn(socialSignupRequest);

        // Create an existing user in the repository
        User existingUser = new User();
        existingUser.setEmail("social@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        userRepository.save(existingUser);

        // when
        ResponseEntity<SignupResponse> responseEntity = signupService.processSocialLoginSignup("GOOGLE", socialLoginSignup);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SignupResponse signupResponse = responseEntity.getBody();
        assertThat(signupResponse).isNotNull();
        assertThat(signupResponse.isSuccess()).isFalse();
        assertThat(signupResponse.getMessage()).isEqualTo("User with this email already exists");

        // Verify interactions with SignupHelper mocks
        verify(signupHelper).getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup);
        verify(signupHelper).getSignupDetails(authUser, socialLoginSignup);

        // Verify only the original user exists (no duplicate was created)
        assertThat(userRepository.findByEmail("social@example.com")).isPresent();
    }

    @Test
    @DisplayName("processSocialLoginSignup should return error response when auth user is null")
    void processSocialLoginSignup_WhenAuthUserIsNull_ShouldReturnErrorResponse() {
        // given
        when(signupHelper.getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup)).thenReturn(null);

        // when
        ResponseEntity<SignupResponse> responseEntity = signupService.processSocialLoginSignup("GOOGLE", socialLoginSignup);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        SignupResponse signupResponse = responseEntity.getBody();
        assertThat(signupResponse).isNotNull();
        assertThat(signupResponse.isSuccess()).isFalse();
        assertThat(signupResponse.getMessage()).isEqualTo("Auth User is null or doesn't have an email address");

        // Verify interactions with SignupHelper mocks
        verify(signupHelper).getUserByAccessToken(AuthType.GOOGLE, socialLoginSignup);
        verify(signupHelper, never()).getSignupDetails(any(), any());

        // Verify no user was saved to repository
        assertThat(userRepository.findByEmail("social@example.com")).isEmpty();
    }
}
