package com.sougata.form_service.dto.question.response;

public record QuestionResDto(
        Long questionId
) {

    public static QuestionResDto create(Long id) {
        return new QuestionResDto(id);
    }

}
