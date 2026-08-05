package com.sougata.form_data_service.service;

import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummariesDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryShortDto;
import com.sougata.form_data_service.dto.question.QuestionSummaryDto;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionResponse;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionSummary;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface FormResponseService {
    FormResponseAddResDto saveResponse(UUID formId, FormResponseAddReqDto req, UUID userId);

    FormResponseSummaryShortDto getFormResponseSummary(UUID formId);

    ResponseSummaryResDto getResponseSummaries(UUID formId);

    ResponseSummaryDto<?> getResponseSummary(UUID formId, Long questionId, Pageable pageable);

    ResponseByQuestionSummary getResponseByQuestionSummary(UUID formId, Long questionId);

    FormResponseSummariesDto getFormResponseSummaries(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable);

    ResponseQuestionDto<? extends ResponseByQuestionResponse> getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable);

    boolean getIsResponseAlreadySubmitted(UUID formId, UUID userId);

    void deleteResponses(UUID formId, Long questionId);
}
