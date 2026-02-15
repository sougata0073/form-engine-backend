package com.sougata.form_service.dto.form;

import com.sougata.form_service.constant.ValidationMessages;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record FormAddUpdateReqDto(
        String title,

        String description,

        @NotNull
        Boolean published,

        @NotNull
        Boolean acceptingResponse,

        String notAcceptingResponseMessage,

        Instant stopAcceptingResponseOn,

        Long stopAcceptingResponseAfterResponse,

        @NotNull(message = ValidationMessages.USER_ID_NOT_NULL)
        UUID userId
) {
}
