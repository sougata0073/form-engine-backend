package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.QuestionRes;

public interface QuestionProjection <Q extends QuestionRes> {
    QuestionType getQuestionType();
    Q getQuestionResponse();
}
