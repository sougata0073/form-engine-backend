package com.sougata.form_service.dto.common;

public record SuccessMessageDto(
        String message
) {
    public static SuccessMessageDto create(String message) {
        return new SuccessMessageDto(message);
    }
}
