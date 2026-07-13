package com.sougata.form_service.dto.question;

import java.util.List;

public record QuestionSummariesResDto(
        List<QuestionSummaryDto> questions
) {
}
