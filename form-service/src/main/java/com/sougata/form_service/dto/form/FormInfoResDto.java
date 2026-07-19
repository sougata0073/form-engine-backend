package com.sougata.form_service.dto.form;

import com.sougata.form_service.model.Form;

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

    public static FormInfoResDto create(Form form) {
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
