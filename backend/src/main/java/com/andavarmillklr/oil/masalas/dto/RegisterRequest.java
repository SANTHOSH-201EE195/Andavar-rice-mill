package com.andavarmillklr.oil.masalas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest DTO - Data sent from the frontend when a new user registers.
 *
 * Example JSON:
 * {
 *   "mobile": "9876543210",
 *   "password": "myPassword123",
 *   "fullName": "Santhosh Kumar",
 *   "email": "santhosh@gmail.com"   // optional
 * }
 */
public class RegisterRequest {

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    /** Email is optional during registration */
    private String email;

    // ========== Constructors ==========

    public RegisterRequest() {
    }

    // ========== Getters and Setters ==========

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
