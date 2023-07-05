package com.telegram.bot.search.house.dto;

import com.telegram.bot.search.house.entity.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class JwtResponse {
    private Long id;
    private String username;
    private Set<Role> roles;
    private SuccessFailure status;
    private String message;

    public enum SuccessFailure {
        SUCCESS, FAILURE
    }

    public JwtResponse(SuccessFailure status, String message, Long id, String username, Set<Role> roles) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public JwtResponse(SuccessFailure status, String message) {
        this.status = status;
        this.message = message;
    }
}
