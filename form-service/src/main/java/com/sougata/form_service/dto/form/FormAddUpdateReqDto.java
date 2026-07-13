package com.sougata.form_service.dto.form;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record FormAddUpdateReqDto(
        String title,

        String description,

        String name,

        @NotNull
        Boolean published,

        @NotNull
        Boolean acceptingResponse,

        String notAcceptingResponseMessage,

        Instant stopAcceptingResponseOn,

        Long stopAcceptingResponseAfterResponse
) {
}
