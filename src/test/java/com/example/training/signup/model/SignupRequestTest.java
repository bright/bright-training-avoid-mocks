package com.example.training.signup.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SignupRequest model.
 * 
 * This test class focuses on testing the validation methods.
 */
class SignupRequestTest {

    private SignupRequest validRequest;
    
    @BeforeEach
    void setUp() {
        // given
        validRequest = new SignupRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setFirstName("Test");
        validRequest.setLastName("User");
        validRequest.setCompanyName("Test Company");
        validRequest.setPassword("password123");
        validRequest.setConfirmPassword("password123");
        validRequest.setCountryCode("US");
        validRequest.setPhoneNumber("1234567890");
    }
    
    @Test
    @DisplayName("isValid should return true for valid request")
    void isValid_WithValidRequest_ShouldReturnTrue() {
        // when
        boolean isValid = validRequest.isValid();
        
        // then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("isValid should return false when email is null")
    void isValid_WithNullEmail_ShouldReturnFalse() {
        // given
        validRequest.setEmail(null);
        
        // when
        boolean isValid = validRequest.isValid();
        
        // then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("isValid should return false when email is empty")
    void isValid_WithEmptyEmail_ShouldReturnFalse() {
        // given
        validRequest.setEmail("");
        
        // when
        boolean isValid = validRequest.isValid();
        
        // then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("isValid should return false when password is null")
    void isValid_WithNullPassword_ShouldReturnFalse() {
        // given
        validRequest.setPassword(null);
        
        // when
        boolean isValid = validRequest.isValid();
        
        // then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("isValid should return false when password is empty")
    void isValid_WithEmptyPassword_ShouldReturnFalse() {
        // given
        validRequest.setPassword("");
        
        // when
        boolean isValid = validRequest.isValid();
        
        // then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("isValid should return false when passwords don't match")
    void isValid_WithNonMatchingPasswords_ShouldReturnFalse() {
        // given
        validRequest.setConfirmPassword("differentPassword");
        
        // when
        boolean isValid = validRequest.isValid();
        
        // then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("isValid with isSocialLogin=true should return true when email is valid")
    void isValid_WithSocialLoginAndValidEmail_ShouldReturnTrue() {
        // given
        validRequest.setPassword(null);
        validRequest.setConfirmPassword(null);
        
        // when
        boolean isValid = validRequest.isValid(true);
        
        // then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("isValid with isSocialLogin=true should return false when email is null")
    void isValid_WithSocialLoginAndNullEmail_ShouldReturnFalse() {
        // given
        validRequest.setEmail(null);
        
        // when
        boolean isValid = validRequest.isValid(true);
        
        // then
        assertThat(isValid).isFalse();
    }
}