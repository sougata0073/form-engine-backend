package com.sougata.form_data_service.dto.response.question;

import com.sougata.form_data_service.dto.response.FormResponseSummaryDto;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

public record AllResponseCountAndIdsResDto(
        @JsonSerialize(using = ToStringSerializer.class)
        Long totalResponseCount,

        List<FormResponseSummaryDto> responses
) {
}
