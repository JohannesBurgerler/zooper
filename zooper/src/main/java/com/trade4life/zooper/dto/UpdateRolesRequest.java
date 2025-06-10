package com.trade4life.zooper.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class UpdateRolesRequest {
    @NotBlank(message = "Roles list cannot be empty!")
    private Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
