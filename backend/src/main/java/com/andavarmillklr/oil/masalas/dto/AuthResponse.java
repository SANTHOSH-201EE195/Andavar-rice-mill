package com.andavarmillklr.oil.masalas.dto;

/**
 * AuthResponse DTO - Data sent back to the frontend after successful login or registration.
 *
 * Contains the JWT token and user information. The frontend stores the token
 * in localStorage and includes it in the Authorization header of every request.
 *
 * Example JSON:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "id": 1,
 *   "mobile": "9876543210",
 *   "fullName": "Santhosh Kumar",
 *   "email": "santhosh@gmail.com",
 *   "role": "ROLE_USER"
 * }
 */
public class AuthResponse {

    /** The JWT token that the frontend must send with every request */
    private String token;

    /** User's database ID */
    private Long id;

    /** User's mobile number */
    private String mobile;

    /** User's display name */
    private String fullName;

    /** User's email (may be null) */
    private String email;

    /** User's role — "ROLE_USER" or "ROLE_ADMIN" */
    private String role;

    // ========== Constructors ==========

    public AuthResponse() {
    }

    /**
     * Create a full auth response with all user details.
     *
     * @param token    The JWT token
     * @param id       User's ID
     * @param mobile   User's mobile number
     * @param fullName User's display name
     * @param email    User's email (can be null)
     * @param role     User's role
     */
    public AuthResponse(String token, Long id, String mobile, String fullName, String email, String role) {
        this.token = token;
        this.id = id;
        this.mobile = mobile;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // ========== Getters and Setters ==========

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
