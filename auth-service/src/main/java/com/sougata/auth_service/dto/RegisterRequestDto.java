package com.sougata.auth_service.dto;

import com.sougata.auth_service.constant.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        String username,

        @NotBlank(message = ValidationMessages.EMPTY_EMAIL)
        @Email(message = ValidationMessages.WRONG_EMAIL_FORMAT)
        String email,

        @NotBlank(message = ValidationMessages.EMPTY_PASSWORD)
        String password,

        String avatarUrl
) {
}
