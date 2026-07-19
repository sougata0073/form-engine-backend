package com.sougata.form_data_service.form_schema.dto.questionSchema.response;

import com.sougata.form_data_service.dto.question.QuestionSummaryDto;

import java.util.List;

public record QuestionSummariesResDto(
        List<QuestionSummaryDto> questions
) {
}
