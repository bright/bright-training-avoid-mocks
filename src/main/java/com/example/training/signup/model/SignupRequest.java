package com.example.training.signup.model;

/**
 * Represents a signup request from a user.
 * This class is used to capture the form data submitted during signup.
 */
public class SignupRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String companyName;
    private String password;
    private String confirmPassword;
    private String countryCode;
    private String phoneNumber;

    public SignupRequest() {
    }

    /**
     * Validates this signup request.
     * 
     * @param isSocialLogin Whether this is a social login request
     * @return true if the request is valid, false otherwise
     */
    public boolean isValid(boolean isSocialLogin) {
        // Basic validation
        if (this.email == null || this.email.isEmpty()) {
            return false;
        }

        // For social login, we don't require password validation
        if (!isSocialLogin) {
            // For regular signup, validate password
            if (this.password == null || this.password.isEmpty()) {
                return false;
            }

            if (!this.password.equals(this.confirmPassword)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates this signup request.
     * 
     * @return true if the request is valid, false otherwise
     */
    public boolean isValid() {
        // Default to regular signup (not social login)
        return isValid(false);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
