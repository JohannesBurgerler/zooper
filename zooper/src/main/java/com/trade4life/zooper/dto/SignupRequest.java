package com.trade4life.zooper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank(message="Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message="username is required")
    @Size(message = "username should be between 5 and 20 characters")
    private String username;

    @NotBlank(message="Password is required")
    @Size(message = "Password should be at least 6 characters")
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
