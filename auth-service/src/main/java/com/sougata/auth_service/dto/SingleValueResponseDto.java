package com.sougata.auth_service.dto;

public record SingleValueResponseDto<T>(
        T value
) {
    public static <T> SingleValueResponseDto<T> of(T value) {
        return new SingleValueResponseDto<>(value);
    }
}
