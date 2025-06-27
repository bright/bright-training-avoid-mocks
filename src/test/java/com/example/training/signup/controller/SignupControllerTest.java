package com.example.training.signup.controller;

import com.example.training.signup.model.SignupRequest;
import com.example.training.signup.model.SignupResponse;
import com.example.training.signup.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for SignupController using @WebMvcTest.
 * 
 * This test class demonstrates the proper way to test Spring MVC controllers
 * using Spring's testing support rather than excessive mocking.
 */
@WebMvcTest(SignupController.class)
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignupService signupService;

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
    void showSignupForm_ShouldAddSignupRequestToModelAndReturnFormView() throws Exception {
        // given

        // when
        mockMvc.perform(get("/signup"))

        // then
                .andExpect(status().isOk())
                .andExpect(view().name("signup/form"))
                .andExpect(model().attributeExists("signupRequest"));
    }

    @Test
    @DisplayName("processSignup should redirect to success URL when signup is successful")
    void processSignup_WhenSuccessful_ShouldRedirectToSuccessUrl() throws Exception {
        // given
        SignupResponse signupResponse = SignupResponse.success("user123", "/dashboard");
        when(signupService.processSignup(any(SignupRequest.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(signupResponse);

        // when
        mockMvc.perform(post("/signup")
                .param("email", signupRequest.getEmail())
                .param("firstName", signupRequest.getFirstName())
                .param("lastName", signupRequest.getLastName())
                .param("password", signupRequest.getPassword())
                .param("confirmPassword", signupRequest.getConfirmPassword()))

        // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attribute("message", "Signup successful!"));
    }

    @Test
    @DisplayName("processSignup should redirect to signup form with error when signup fails")
    void processSignup_WhenFails_ShouldRedirectToSignupFormWithError() throws Exception {
        // given
        String errorMessage = "Invalid email format";
        SignupResponse signupResponse = SignupResponse.failure(errorMessage);
        when(signupService.processSignup(any(SignupRequest.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(signupResponse);

        // when
        mockMvc.perform(post("/signup")
                .param("email", signupRequest.getEmail())
                .param("firstName", signupRequest.getFirstName())
                .param("lastName", signupRequest.getLastName())
                .param("password", signupRequest.getPassword())
                .param("confirmPassword", signupRequest.getConfirmPassword()))

        // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attribute("error", errorMessage))
                .andExpect(flash().attributeExists("signupRequest"));
    }

}
