package com.sougata.form_data_service.service;

import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryResDto;
import com.sougata.form_data_service.dto.question.QuestionSummaryDto;
import com.sougata.form_data_service.dto.response.question.AllResponseCountAndIdsResDto;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionResponse;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionSummary;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface FormResponseService {
    FormResponseAddResDto saveResponse(UUID formId, FormResponseAddReqDto req, UUID userId);

    FormResponseSummaryResDto getFormResponseSummary(UUID formId);

    ResponseSummaryResDto getResponseSummaries(UUID formId);

    ResponseByQuestionSummary getResponseByQuestionSummary(UUID formId, Long questionId);

    ResponseQuestionDto<? extends ResponseByQuestionResponse> getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable);

    boolean getIsResponseAlreadySubmitted(UUID formId, UUID userId);

    void deleteResponses(UUID formId, QuestionSummaryDto body);

    AllResponseCountAndIdsResDto getAllResponseCountAndIds(UUID formId);
}
