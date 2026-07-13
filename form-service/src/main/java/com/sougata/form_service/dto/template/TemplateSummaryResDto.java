package com.sougata.form_service.dto.template;

import java.util.UUID;

public record TemplateSummaryResDto(
        UUID id,
        String name,
        String categoryName
) {
}
