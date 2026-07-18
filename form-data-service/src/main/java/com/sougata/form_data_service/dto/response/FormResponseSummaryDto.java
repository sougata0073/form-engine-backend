package com.sougata.form_data_service.dto.response;

import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.UUID;

public record FormResponseSummaryDto(
        @JsonSerialize(using = ToStringSerializer.class)
        Long responseId,
        UUID responderId,
        String responderUserName,
        String responderEmail,
        String responderAvatarUrl
) {
}
