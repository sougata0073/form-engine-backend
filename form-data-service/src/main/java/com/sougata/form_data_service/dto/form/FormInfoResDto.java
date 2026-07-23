package com.sougata.form_data_service.dto.form;

import java.time.Instant;
import java.util.UUID;

public record FormInfoResDto(
        UUID id,
        String name,
        String title,
        String description,
        Boolean published,
        Boolean acceptingResponse,
        String notAcceptingResponseMessage,
        Instant stopAcceptingResponseOn,
        Integer stopAcceptingResponseAfterResponse,
        Instant lastOpenedOn
) {

}
