package com.sougata.form_service.dto.form;

import java.time.Instant;
import java.util.UUID;

public record FormSummaryResDto(
        UUID id,
        String name,
        Instant lastOpenedOn
) {
}
