package com.sougata.form_data_service.formValidation.service;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.QuestionResponseAddReq;
import com.sougata.form_data_service.dto.question.response.QuestionRes;

public abstract class QuestionSchemaManager<QR extends QuestionRes, QRAR extends QuestionResponseAddReq> {

    public abstract boolean validateResponse(QRAR questionResponseAddReq, QR questionRes);

    public abstract QuestionType getQuestionType();
}
