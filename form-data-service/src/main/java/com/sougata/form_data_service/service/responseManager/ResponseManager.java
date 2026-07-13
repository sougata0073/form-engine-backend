package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.QuestionResponseAddReq;
import com.sougata.form_data_service.dto.question.response.QuestionRes;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;

import java.util.List;
import java.util.UUID;

public abstract class ResponseManager<
        QR extends QuestionResponseAddReq, RS extends ResponseSummaryDto, QRes extends QuestionRes, ResByQ extends ResponseQuestionDto
        > {

    public abstract void create(QR response, FormResponse formResponse);

    public abstract List<RS> getResponseSummaries(UUID formId, List<QRes> questionResponses);

    public abstract ResByQ getResponseByQuestion(UUID formId, QRes questionRes);

    public abstract QuestionType getQuestionType();

    public abstract void deleteResponses(UUID formId, Long questionId);

}
