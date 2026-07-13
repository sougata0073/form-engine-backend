package com.sougata.form_data_service.dto.question;


import com.sougata.form_data_service.constant.QuestionType;

public record QuestionSummaryDto(
        Long id,
        String question,
        QuestionType questionType,
        Integer orderIndex
) {
}
