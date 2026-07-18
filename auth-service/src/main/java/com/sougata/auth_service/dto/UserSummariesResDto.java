package com.sougata.auth_service.dto;

import java.util.List;

public record UserSummariesResDto(
        List<UserSummaryDto> users
) {
}
