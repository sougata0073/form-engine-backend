package com.sougata.form_data_service.dto.validation.request;

import java.util.List;

public record TickBoxGridRowValidationRequestDto(
        List<Integer> responseIndexes
) {
}
