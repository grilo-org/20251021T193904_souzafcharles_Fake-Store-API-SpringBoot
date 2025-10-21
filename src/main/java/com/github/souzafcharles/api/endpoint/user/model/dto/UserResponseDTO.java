package com.github.souzafcharles.api.endpoint.user.model.dto;

import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import java.io.Serializable;

public record UserResponseDTO(
        String id,
        String username,
        String email
) implements Serializable {
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}