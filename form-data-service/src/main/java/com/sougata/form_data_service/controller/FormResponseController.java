package com.sougata.form_data_service.controller;

import com.sougata.form_data_service.dto.common.SuccessMessageDto;
import com.sougata.form_data_service.dto.form.FormResponsePutReqDto;
import com.sougata.form_data_service.dto.form.FormResponsePutResDto;
import com.sougata.form_data_service.service.FormResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(path = "{formId}/response")
    @Caching(evict = {
            @CacheEvict(cacheNames = {CACHE_FORM_RESPONSE_SUMMARY_SHORT, CACHE_RESPONSE_SUMMARIES}, key = "#formId"),
            @CacheEvict(
                    cacheNames = {CACHE_RESPONSE_BY_QUESTION, CACHE_FORM_RESPONSE_SUMMARIES, CACHE_RESPONSE_SUMMARY, CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE},
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

    @DeleteMapping(path = "{formId}/users/{userId}/responses/{formResponseId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {CACHE_FORM_RESPONSE_SUMMARY_SHORT, CACHE_RESPONSE_SUMMARIES}, key = "#formId"),
            @CacheEvict(cacheNames = {CACHE_IS_RESPONSE_ALREADY_SUBMITTED}, key = "{#formId, #userId}"),
            @CacheEvict(cacheNames = {CACHE_INDIVIDUAL_FORM_RESPONSE}, key = "{#formId, #formResponseId}"),
            @CacheEvict(cacheNames = {CACHE_RESPONSE_BY_QUESTION, CACHE_FORM_RESPONSE_SUMMARIES, CACHE_RESPONSE_SUMMARY, CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE},
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
                    cacheNames = {CACHE_RESPONSE_BY_QUESTION, CACHE_FORM_RESPONSE_SUMMARIES, CACHE_RESPONSE_SUMMARY, CACHE_INDIVIDUAL_FORM_RESPONSE, CACHE_INDIVIDUAL_FORM_RESPONSE_BY_PAGE},
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
