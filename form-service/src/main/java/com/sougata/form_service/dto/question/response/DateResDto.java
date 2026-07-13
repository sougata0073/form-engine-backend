package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.Date;

public class DateResDto extends QuestionRes {

    public DateResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType) {
        super(id, question, description, required, orderIndex, questionType);
    }

    public static DateResDto create(Date date) {
        return new DateResDto(
                date.getId(),
                date.getQuestion(),
                date.getDescription(),
                date.getRequired(),
                date.getOrderIndex(),
                QuestionType.DATE
        );
    }
}
