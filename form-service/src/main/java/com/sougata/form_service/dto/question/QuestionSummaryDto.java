package com.sougata.form_service.dto.question;

import com.sougata.form_service.constant.QuestionType;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record QuestionSummaryDto(
        @JsonSerialize(using = ToStringSerializer.class)
        Long id,
        String question,
        QuestionType questionType,
        Integer orderIndex
) {
}
