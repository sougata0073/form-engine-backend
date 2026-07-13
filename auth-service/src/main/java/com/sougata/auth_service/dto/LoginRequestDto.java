package com.sougata.auth_service.dto;

import com.sougata.auth_service.constant.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank
        String email,

        @NotBlank(message = ValidationMessages.EMPTY_PASSWORD)
        String password
) {
}
