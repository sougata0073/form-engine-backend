package com.sougata.form_service.dto.question.response;

import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record QuestionAddUpdateResDto(
        @JsonSerialize(using = ToStringSerializer.class)
        Long questionId,

        String message,
        Integer orderIndex
) {

    public static QuestionAddUpdateResDto create(Long questionId, String message, Integer orderIndex) {
        return new QuestionAddUpdateResDto(questionId, message, orderIndex);
    }

}
