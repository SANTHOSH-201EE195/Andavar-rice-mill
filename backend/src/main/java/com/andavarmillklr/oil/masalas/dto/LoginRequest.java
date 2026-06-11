package com.andavarmillklr.oil.masalas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * LoginRequest DTO - Data sent from the frontend when a user logs in.
 *
 * The user submits their mobile number and password. This class validates
 * that both fields are present and meet minimum requirements.
 *
 * Example JSON:
 * {
 *   "mobile": "9876543210",
 *   "password": "myPassword123"
 * }
 */
public class LoginRequest {

    /** Mobile number — must not be blank */
    @NotBlank(message = "Mobile number is required")
    private String mobile;

    /** Password — must not be blank and at least 6 characters */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // ========== Constructors ==========

    public LoginRequest() {
    }

    public LoginRequest(String mobile, String password) {
        this.mobile = mobile;
        this.password = password;
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
}
