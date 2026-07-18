package com.sougata.form_data_service.dto.user;

import java.util.UUID;

public record UserSummaryDto(
        UUID userId,
        String userName,
        String email,
        String avatarUrl
) {
}
