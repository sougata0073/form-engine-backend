package com.sougata.form_data_service.dto.response.summary;

import java.util.List;

public record ResponseSummaryResDto(
        List<ResponseSummaryDto> responses
) {
}
