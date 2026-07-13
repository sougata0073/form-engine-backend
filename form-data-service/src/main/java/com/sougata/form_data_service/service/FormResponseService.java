package com.sougata.form_data_service.service;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryResDto;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;

import java.util.UUID;

public interface FormResponseService {
    FormResponseAddResDto saveResponse(UUID formId, FormResponseAddReqDto req, UUID userId);

    FormResponseSummaryResDto getFormResponseSummary(UUID formId);

    ResponseSummaryResDto getResponseSummaries(UUID formId);

    ResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId);

    boolean getIsResponseAlreadySubmitted(UUID formId, UUID userId);

    void deleteResponses(UUID formId, Long questionId, QuestionType questionType);
}
