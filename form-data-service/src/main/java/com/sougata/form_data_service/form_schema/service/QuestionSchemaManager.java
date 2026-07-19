package com.sougata.form_data_service.form_schema.service;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.QuestionResponseAddReq;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.QuestionRes;
import com.sougata.form_data_service.form_schema.model.QuestionSchema;

import java.util.UUID;

public abstract class QuestionSchemaManager<Q extends QuestionSchema, QR extends QuestionRes, V extends QuestionResponseAddReq> {

    public abstract QR get(UUID formId, Long questionId);

    public abstract boolean validateResponse(V validationDto);

    public abstract QuestionType getQuestionType();

    public abstract QR toQuestionResDto(Q questionSchema);

    public void populateCommonFields(QuestionSchema questionSchema, QuestionRes questionRes) {
        questionRes.setId(questionSchema.getId());
        questionRes.setQuestion(questionSchema.getQuestion());
        questionRes.setQuestionType(getQuestionType());
        questionRes.setDescription(questionSchema.getDescription());
        questionRes.setOrderIndex(questionSchema.getOrderIndex());
        questionRes.setRequired(questionSchema.getRequired());
    }

}
