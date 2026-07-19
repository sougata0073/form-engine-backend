package com.sougata.form_data_service.dto.form;

import com.sougata.form_data_service.form_schema.model.FormSchema;

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

    public static FormInfoResDto create(FormSchema form) {
        return new FormInfoResDto(
                form.getId(),
                form.getName(),
                form.getTitle(),
                form.getDescription(),
                form.getPublished(),
                form.getAcceptingResponse(),
                form.getNotAcceptingResponseMessage(),
                form.getStopAcceptingResponseOn(),
                form.getStopAcceptingResponseAfterResponse(),
                form.getLastOpenedOn()
        );
    }

}
