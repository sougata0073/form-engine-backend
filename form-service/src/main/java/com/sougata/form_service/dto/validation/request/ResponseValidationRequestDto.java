package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ResponseValidationRequestDto(
        @NotNull(message = ValidationRequestValidationMessages.RESPONSE_LIST_NOT_NULL)
        List<@Valid ValidationRequest> responses
) {
}
