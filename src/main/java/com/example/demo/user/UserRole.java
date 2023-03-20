package com.example.demo.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UserRole(String description) {
        this.description = description;
    }

    private String description;
}
