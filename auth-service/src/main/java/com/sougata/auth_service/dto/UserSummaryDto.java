package com.sougata.auth_service.dto;

import java.util.UUID;

public record UserSummaryDto(
        UUID userId,
        String userName,
        String email,
        String avatarUrl
) {
}
