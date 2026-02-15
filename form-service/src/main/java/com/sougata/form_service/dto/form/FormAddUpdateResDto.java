package com.sougata.form_service.dto.form;

import com.sougata.form_service.model.Form;

import java.time.Instant;
import java.util.UUID;

public record FormAddUpdateResDto(
        UUID id,
        String title,
        String description,
        Boolean published,
        Boolean acceptingResponse,
        String notAcceptingResponseMessage,
        Instant stopAcceptingResponseOn,
        Long stopAcceptingResponseAfterResponse
) {

    public static FormAddUpdateResDto create(Form form) {
        return new FormAddUpdateResDto(
                form.getId(),
                form.getTitle(),
                form.getDescription(),
                form.getPublished(),
                form.getAcceptingResponse(),
                form.getNotAcceptingResponseMessage(),
                form.getStopAcceptingResponseOn(),
                form.getStopAcceptingResponseAfterResponse()
        );
    }
}


