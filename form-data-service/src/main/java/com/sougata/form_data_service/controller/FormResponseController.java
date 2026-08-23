package com.sougata.form_data_service.controller;

import com.sougata.form_data_service.dto.common.SuccessMessageDto;
import com.sougata.form_data_service.dto.form.FormResponsePutReqDto;
import com.sougata.form_data_service.dto.form.FormResponsePutResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummariesDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryShortDto;
import com.sougata.form_data_service.dto.response.individual.ResponseIndividualResDto;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionResponse;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionSummary;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import com.sougata.form_data_service.service.FormResponseService;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/forms")
@CrossOrigin
public class FormResponseController {

    private static final String CACHE_FORM_RESPONSE_SUMMARY_SHORT = "formResponseSummaryShort";
    private static final String CACHE_RESPONSE_SUMMARIES = "responseSummaries";
    private static final String CACHE_RESPONSE_BY_QUESTION_SUMMARY = "responseByQuestionSummary";
    private static final String CACHE_RESPONSE_BY_QUESTION = "responseByQuestion";
    private static final String CACHE_FORM_RESPONSE_SUMMARIES = "formResponseSummaries";
    private static final String CACHE_RESPONSE_SUMMARY = "responseSummary";
    private static final String CACHE_INDIVIDUAL_FORM_RESPONSE = "individualFormResponse";
    private static final String CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE = "individualFormResponseByPage";
    private static final String CACHE_IS_RESPONSE_ALREADY_SUBMITTED = "isResponseAlreadySubmitted";

    private final FormResponseService formResponseService;

    public FormResponseController(FormResponseService formResponseService) {
        this.formResponseService = formResponseService;
    }

    @PostMapping(path = "{formId}/response")
    @Caching(evict = {
            @CacheEvict(cacheNames = {CACHE_FORM_RESPONSE_SUMMARY_SHORT, CACHE_RESPONSE_SUMMARIES}, key = "#formId"),
            @CacheEvict(
                    cacheNames = {CACHE_RESPONSE_BY_QUESTION_SUMMARY, CACHE_RESPONSE_BY_QUESTION, CACHE_FORM_RESPONSE_SUMMARIES, CACHE_RESPONSE_SUMMARY, CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE},
                    allEntries = true
            )
    })
    public ResponseEntity<FormResponsePutResDto> addFormResponse(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormResponsePutReqDto dto,
            @RequestHeader("auth-jwt") UUID authJwt
    ) {
        var res = formResponseService.saveResponse(formId, dto, authJwt);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping(path = "{formId}/form-response-summary")
    @Cacheable(cacheNames = {CACHE_FORM_RESPONSE_SUMMARY_SHORT}, key = "#formId")
    public FormResponseSummaryShortDto getFormResponseSummary(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getFormResponseSummaryShort(formId);
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

    @GetMapping(path = "{formId}/questions/{questionId}/response-summary-by-question")
    @Cacheable(cacheNames = {CACHE_RESPONSE_BY_QUESTION_SUMMARY}, key = "{#formId, #questionId}")
    public ResponseByQuestionSummary getResponseByQuestionSummary(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return formResponseService.getResponseByQuestionSummary(formId, questionId);
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

    @GetMapping(path = "{formId}/is-response-already-submitted", params = "userId")
    @Cacheable(cacheNames = {CACHE_IS_RESPONSE_ALREADY_SUBMITTED}, key = "{#formId, #userId}")
    public boolean getIsResponseAlreadySubmitted(
            @PathVariable("formId") UUID formId,
            @RequestParam("userId") UUID userId
    ) {
        return formResponseService.getIsResponseAlreadySubmitted(formId, userId);
    }

    @DeleteMapping(path = "{formId}/users/{userId}/responses/{formResponseId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {CACHE_FORM_RESPONSE_SUMMARY_SHORT, CACHE_RESPONSE_SUMMARIES}, key = "#formId"),
            @CacheEvict(cacheNames = {CACHE_IS_RESPONSE_ALREADY_SUBMITTED}, key = "{#formId, #userId}"),
            @CacheEvict(cacheNames = {CACHE_INDIVIDUAL_FORM_RESPONSE}, key = "{#formId, #formResponseId}"),
            @CacheEvict(cacheNames = {CACHE_RESPONSE_BY_QUESTION_SUMMARY, CACHE_RESPONSE_BY_QUESTION, CACHE_FORM_RESPONSE_SUMMARIES, CACHE_RESPONSE_SUMMARY, CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE},
                    allEntries = true
            )
    })
    public SuccessMessageDto deleteFormResponse(
            @PathVariable("formId") UUID formId,
            @PathVariable("userId") UUID userId,
            @PathVariable("formResponseId") Long formResponseId
    ) {
        return formResponseService.deleteFormResponse(formId, userId, formResponseId);
    }

    @DeleteMapping(path = "{formId}/questions/{questionId}/responses")
    @Caching(evict = {
            @CacheEvict(cacheNames = {CACHE_FORM_RESPONSE_SUMMARY_SHORT, CACHE_RESPONSE_SUMMARIES}, key = "#formId"),
            @CacheEvict(
                    cacheNames = {CACHE_RESPONSE_BY_QUESTION_SUMMARY, CACHE_RESPONSE_BY_QUESTION, CACHE_FORM_RESPONSE_SUMMARIES, CACHE_RESPONSE_SUMMARY, CACHE_INDIVIDUAL_FORM_RESPONSE, CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE},
                    allEntries = true
            )
    })
    public void deleteQuestionResponses(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        formResponseService.deleteQuestionResponses(formId, questionId);
    }

}
