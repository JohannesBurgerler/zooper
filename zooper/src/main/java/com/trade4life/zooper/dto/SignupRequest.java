package com.trade4life.zooper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class SignupRequest {
    @NotBlank(message="Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message="username is required")
    @Size(min = 3, max = 20, message = "username should be between 5 and 20 characters")
    private String username;

    @NotBlank(message="Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;

    private Set<String> roles;

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

    public Set<String> getRole() {
        return roles;
    }

    public void setRole(Set<String> roles) {
        this.roles = roles;
    }
}
