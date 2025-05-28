package com.example.training.signup.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for User model.
 * 
 * This test class focuses on testing the static factory method fromSignupRequest.
 */
class UserTest {

    private SignupRequest signupRequest;
    private String ipAddress;

    @BeforeEach
    void setUp() {
        // given
        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setCompanyName("Test Company");
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");
        signupRequest.setCountryCode("US");
        signupRequest.setPhoneNumber("1234567890");
        
        ipAddress = "127.0.0.1";
    }

    @Test
    @DisplayName("fromSignupRequest should create a User with correct properties")
    void fromSignupRequest_ShouldCreateUserWithCorrectProperties() {
        // when
        User user = User.fromSignupRequest(signupRequest, ipAddress);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getCompanyName()).isEqualTo("Test Company");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(user.getCountryCode()).isEqualTo("US");
        assertThat(user.getPhoneNumber()).isEqualTo("1234567890");
    }

    @Test
    @DisplayName("fromSignupRequest should handle null values gracefully")
    void fromSignupRequest_ShouldHandleNullValuesGracefully() {
        // given
        signupRequest.setEmail(null);
        signupRequest.setFirstName(null);
        signupRequest.setLastName(null);
        signupRequest.setCompanyName(null);
        signupRequest.setPassword(null);
        signupRequest.setCountryCode(null);
        signupRequest.setPhoneNumber(null);
        
        // when
        User user = User.fromSignupRequest(signupRequest, null);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getCompanyName()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getIpAddress()).isNull();
        assertThat(user.getCountryCode()).isNull();
        assertThat(user.getPhoneNumber()).isNull();
    }
}