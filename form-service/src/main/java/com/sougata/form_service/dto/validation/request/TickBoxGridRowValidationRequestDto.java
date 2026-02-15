package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TickBoxGridRowValidationRequestDto(
        @NotNull(message = ValidationRequestValidationMessages.RESPONSE_INDEX_NOT_NULL)
        @Min(value = 0, message = ValidationRequestValidationMessages.RESPONSE_INDEX_INVALID_RANGE)
        @Max(value = 19, message = ValidationRequestValidationMessages.RESPONSE_INDEX_INVALID_RANGE)
        List<Integer> responseIndexes
) {
}
