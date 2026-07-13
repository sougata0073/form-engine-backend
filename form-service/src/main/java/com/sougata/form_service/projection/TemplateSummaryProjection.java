package com.sougata.form_service.projection;

import java.util.UUID;

public record TemplateSummaryProjection(
        UUID id,
        String name,
        String categoryName
) {

}
