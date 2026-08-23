package com.sougata.form_response_service.controller;

import com.sougata.form_engine.dto.form.FormResponseCountDto;
import com.sougata.form_engine.dto.form.FormResponseSummariesDto;
import com.sougata.form_engine.dto.formResponse.individual.ResponseIndividualResDto;
import com.sougata.form_engine.dto.formResponse.question.ResponseByQuestionResponse;
import com.sougata.form_engine.dto.formResponse.question.ResponseQuestionDto;
import com.sougata.form_engine.dto.formResponse.summary.ResponseSummaryDto;
import com.sougata.form_engine.dto.formResponse.summary.ResponseSummaryResDto;
import com.sougata.form_response_service.service.FormResponseService;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/forms")
@CrossOrigin
@RequiredArgsConstructor
public class FormResponseController {

    private static final String CACHE_FORM_RESPONSE_SUMMARY_SHORT = "formResponseSummaryShort";
    private static final String CACHE_RESPONSE_SUMMARIES = "responseSummaries";
    private static final String CACHE_RESPONSE_BY_QUESTION = "responseByQuestion";
    private static final String CACHE_FORM_RESPONSE_SUMMARIES = "formResponseSummaries";
    private static final String CACHE_RESPONSE_SUMMARY = "responseSummary";
    private static final String CACHE_INDIVIDUAL_FORM_RESPONSE = "individualFormResponse";
    private static final String CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE = "individualFormResponseByPage";
    private static final String CACHE_IS_RESPONSE_ALREADY_SUBMITTED = "isResponseAlreadySubmitted";

    private final FormResponseService formResponseService;

    @GetMapping(path = "{formId}/is-response-already-submitted", params = "userId")
    @Cacheable(cacheNames = {CACHE_IS_RESPONSE_ALREADY_SUBMITTED}, key = "{#formId, #userId}")
    public boolean getIsResponseAlreadySubmitted(
            @PathVariable("formId") UUID formId,
            @RequestParam("userId") UUID userId
    ) {
        return formResponseService.getIsResponseAlreadySubmitted(formId, userId);
    }

    @GetMapping(path = "{formId}/form-response-count")
    @Cacheable(cacheNames = {CACHE_FORM_RESPONSE_SUMMARY_SHORT}, key = "#formId")
    public FormResponseCountDto getFormResponseCount(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getFormResponseCount(formId);
    }

    @GetMapping(path = "{formId}/response-summaries")
    @Cacheable(cacheNames = {CACHE_RESPONSE_SUMMARIES}, key = "#formId")
    public ResponseSummaryResDto getResponseSummaries(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getResponseSummaries(formId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/response-summary")
    @Cacheable(
            cacheNames = {CACHE_RESPONSE_SUMMARY},
            key = "{#formId, #questionId, #pageable.pageNumber, #pageable.pageSize}"
    )
    public ResponseSummaryDto<?> getResponseSummary(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            Pageable pageable
    ) {
        return formResponseService.getResponseSummary(formId, questionId, pageable);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/form-response-summaries", params = {"formResponsesIdentifier"})
    @Cacheable(
            cacheNames = {CACHE_FORM_RESPONSE_SUMMARIES},
            key = "{#formId, #questionId, #formResponsesIdentifier, #pageable.pageNumber, #pageable.pageSize}"
    )
    public FormResponseSummariesDto getFormResponseSummaries(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @RequestParam("formResponsesIdentifier") String formResponsesIdentifier,
            Pageable pageable
    ) {
        return formResponseService.getFormResponseSummaries(formId, questionId, formResponsesIdentifier, pageable);
    }

    @GetMapping(path = "{formId}/responses/{formResponseId}")
    @Cacheable(
            cacheNames = {CACHE_INDIVIDUAL_FORM_RESPONSE},
            key = "{#formId, #formResponseId}"
    )
    public ResponseIndividualResDto getIndividualFormResponse(
            @PathVariable("formId") UUID formId,
            @PathVariable("formResponseId") Long formResponseId
    ) {
        return formResponseService.getIndividualFormResponse(formId, formResponseId);
    }

    // Page number starts from 0
    @GetMapping(path = "{formId}/responses", params = {"page"})
    @Cacheable(
            cacheNames = {CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE},
            key = "{#formId, #page}"
    )
    public ResponseIndividualResDto getIndividualFormResponseByPage(
            @PathVariable("formId") UUID formId,
            @QueryParam("page") Long page
    ) {
        return formResponseService.getIndividualFormResponseByPage(formId, page);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/response")
    @Cacheable(
            cacheNames = {CACHE_RESPONSE_BY_QUESTION},
            key = "{#formId, #questionId, #extraParams.get('rowId') ?: 0, #pageable.pageNumber, #pageable.pageSize}"
    )
    public ResponseQuestionDto<? extends ResponseByQuestionResponse> getResponseByQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @RequestParam Map<String, String> extraParams,
            Pageable pageable
    ) {
        return formResponseService.getResponseByQuestion(formId, questionId, extraParams, pageable);
    }

}
