package com.github.souzafcharles.api.endpoint.user.model.dto;

import com.github.souzafcharles.api.utils.Messages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record UserRequestDTO(
        @NotBlank(message = Messages.USERNAME_REQUIRED) String username,
        @NotBlank(message = Messages.EMAIL_REQUIRED)
        @Email(message = Messages.EMAIL_INVALID) String email,
        @NotBlank(message = Messages.PASSWORD_REQUIRED)
        @Size(min = 6, message = Messages.PASSWORD_MIN_LENGTH) String password
) implements Serializable { }