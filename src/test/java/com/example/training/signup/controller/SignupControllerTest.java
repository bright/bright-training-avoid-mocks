package com.example.training.signup.controller;

import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SignupResponse;
import com.example.training.signup.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for SignupController.
 * 
 * PROBLEM: This test class demonstrates the abuse of mocks, with excessive mocking
 * and verification of implementation details rather than behavior.
 */
@ExtendWith(MockitoExtension.class)
class SignupControllerTest {

    @Mock
    private SignupService signupService;
    
    @Mock
    private Model model;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private RedirectAttributes redirectAttributes;
    
    @InjectMocks
    private SignupController signupController;
    
    private SignupRequest signupRequest;
    
    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");
    }
    
    @Test
    @DisplayName("showSignupForm should add signupRequest to model and return form view")
    void showSignupForm_ShouldAddSignupRequestToModelAndReturnFormView() {
        // Act
        String viewName = signupController.showSignupForm(model);
        
        // Assert
        assertEquals("signup/form", viewName);
        
        // Verify interactions with mocks
        verify(model).addAttribute(eq("signupRequest"), any(SignupRequest.class));
    }
    
    @Test
    @DisplayName("processSignup should redirect to success URL when signup is successful")
    void processSignup_WhenSuccessful_ShouldRedirectToSuccessUrl() {
        // Arrange
        SignupResponse signupResponse = SignupResponse.success("user123", "/dashboard");
        when(signupService.processSignup(any(SignupRequest.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(signupResponse);
        
        // Act
        String viewName = signupController.processSignup(signupRequest, request, response, redirectAttributes);
        
        // Assert
        assertEquals("redirect:/dashboard", viewName);
        
        // Verify interactions with mocks
        verify(signupService).processSignup(signupRequest, request, response);
        verify(redirectAttributes).addFlashAttribute("message", "Signup successful!");
        verifyNoMoreInteractions(redirectAttributes);
    }
    
    @Test
    @DisplayName("processSignup should redirect to signup form with error when signup fails")
    void processSignup_WhenFails_ShouldRedirectToSignupFormWithError() {
        // Arrange
        SignupResponse signupResponse = SignupResponse.failure("Invalid email format");
        when(signupService.processSignup(any(SignupRequest.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(signupResponse);
        
        // Act
        String viewName = signupController.processSignup(signupRequest, request, response, redirectAttributes);
        
        // Assert
        assertEquals("redirect:/signup", viewName);
        
        // Verify interactions with mocks
        verify(signupService).processSignup(signupRequest, request, response);
        verify(redirectAttributes).addFlashAttribute("error", "Invalid email format");
        verify(redirectAttributes).addFlashAttribute("signupRequest", signupRequest);
        verifyNoMoreInteractions(redirectAttributes);
    }
    
    @Test
    @DisplayName("processSignupDirect should redirect to success URL when signup is successful")
    void processSignupDirect_WhenSuccessful_ShouldRedirectToSuccessUrl() {
        // Arrange
        SignupResponse signupResponse = SignupResponse.success("user123", "/dashboard");
        when(signupService.processSignup(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(signupResponse);
        
        // Act
        String viewName = signupController.processSignupDirect(request, response, redirectAttributes);
        
        // Assert
        assertEquals("redirect:/dashboard", viewName);
        
        // Verify interactions with mocks
        verify(signupService).processSignup(request, response);
        verify(redirectAttributes).addFlashAttribute("message", "Signup successful!");
        verifyNoMoreInteractions(redirectAttributes);
    }
    
    @Test
    @DisplayName("processSignupDirect should redirect to signup form with error when signup fails")
    void processSignupDirect_WhenFails_ShouldRedirectToSignupFormWithError() {
        // Arrange
        SignupResponse signupResponse = SignupResponse.failure("Invalid email format");
        when(signupService.processSignup(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(signupResponse);
        
        // Act
        String viewName = signupController.processSignupDirect(request, response, redirectAttributes);
        
        // Assert
        assertEquals("redirect:/signup", viewName);
        
        // Verify interactions with mocks
        verify(signupService).processSignup(request, response);
        verify(redirectAttributes).addFlashAttribute("error", "Invalid email format");
        verifyNoMoreInteractions(redirectAttributes);
    }
}