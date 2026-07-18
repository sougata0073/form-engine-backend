package com.sougata.form_data_service.dto.user;

import java.util.List;

public record UserSummariesResDto(
        List<UserSummaryDto> users
) {
}
