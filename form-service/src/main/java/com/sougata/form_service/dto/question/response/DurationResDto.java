package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.Duration;

public class DurationResDto extends QuestionRes {

    public DurationResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType) {
        super(id, question, description, required, orderIndex, questionType);
    }

    public static DurationResDto create(Duration duration) {
        return new DurationResDto(
                duration.getId(),
                duration.getQuestion(),
                duration.getDescription(),
                duration.getRequired(),
                duration.getOrderIndex(),
                QuestionType.DURATION
        );
    }
}

