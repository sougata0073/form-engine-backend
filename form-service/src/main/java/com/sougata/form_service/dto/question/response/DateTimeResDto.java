package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.DateTime;

public class DateTimeResDto extends QuestionRes {

    public DateTimeResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType) {
        super(id, question, description, required, orderIndex, questionType);
    }

    public static DateTimeResDto create(DateTime dateTime) {
        return new DateTimeResDto(
                dateTime.getId(),
                dateTime.getQuestion(),
                dateTime.getDescription(),
                dateTime.getRequired(),
                dateTime.getOrderIndex(),
                QuestionType.DATE_TIME
        );
    }
}

