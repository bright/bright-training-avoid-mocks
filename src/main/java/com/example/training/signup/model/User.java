package com.example.training.signup.model;

import java.util.UUID;

/**
 * Represents a user in the system.
 * This is a simple model class for demonstration purposes.
 */
public class User {

    /**
     * Creates a User from a SignupRequest and IP address.
     * 
     * @param signupRequest The signup request containing user information
     * @param ipAddress The IP address of the user
     * @return A new User instance
     */
    public static User fromSignupRequest(SignupRequest signupRequest, String ipAddress) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setCompanyName(signupRequest.getCompanyName());
        user.setPassword(signupRequest.getPassword()); // In a real app, this would be hashed
        user.setIpAddress(ipAddress);
        user.setCountryCode(signupRequest.getCountryCode());
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        return user;
    }
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String companyName;
    private String password;
    private String ipAddress;
    private String countryCode;
    private String phoneNumber;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
