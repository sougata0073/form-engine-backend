package com.sougata.form_data_service.controller;

import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryResDto;
import com.sougata.form_data_service.dto.question.QuestionSummaryDto;
import com.sougata.form_data_service.dto.response.question.AllResponseCountAndIdsResDto;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionResponse;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionSummary;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import com.sougata.form_data_service.service.FormResponseService;
import jakarta.validation.Valid;
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

    private final FormResponseService formResponseService;

    public FormResponseController(FormResponseService formResponseService) {
        this.formResponseService = formResponseService;
    }

    @PostMapping(path = "{formId}/response")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"responseSummary", "responseSummaries", "responseCountAndIds"}, key = "#formId"),
            @CacheEvict(cacheNames = {"responseByQuestionSummary", "responseByQuestion"}, allEntries = true)
    })
    public ResponseEntity<FormResponseAddResDto> addFormResponse(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormResponseAddReqDto dto,
            @RequestHeader("auth-jwt") UUID authJwt
    ) {
        var res = formResponseService.saveResponse(formId, dto, authJwt);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping(path = "{formId}/form-response-summary")
    @Cacheable(cacheNames = {"responseSummary"}, key = "#formId")
    public FormResponseSummaryResDto getFormResponseSummary(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getFormResponseSummary(formId);
    }

    @GetMapping(path = "{formId}/response-summaries")
    @Cacheable(cacheNames = {"responseSummaries"}, key = "#formId")
    public ResponseSummaryResDto getResponseSummaries(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getResponseSummaries(formId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/response-summary")
    @Cacheable(cacheNames = {"responseByQuestionSummary"}, key = "{#formId, #questionId}")
    public ResponseByQuestionSummary getResponseByQuestionSummary(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return formResponseService.getResponseByQuestionSummary(formId, questionId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/response")
    @Cacheable(
            cacheNames = {"responseByQuestion"},
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

    @GetMapping(path = "{formId}/is-response-already-submitted", params = "userId")
    // TODO: Enable caching when delete form response endpoint is created
    public boolean getIsResponseAlreadySubmitted(
            @PathVariable("formId") UUID formId,
            @RequestParam("userId") UUID userId
    ) {
        return formResponseService.getIsResponseAlreadySubmitted(formId, userId);
    }

    @PostMapping(path = "{formId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"responseSummary", "responseSummaries"}, key = "#formId"),
            @CacheEvict(cacheNames = {"responseByQuestionSummary", "responseByQuestion"}, allEntries = true)
    })
    public void deleteQuestionResponses(
            @PathVariable("formId") UUID formId,
            @RequestBody QuestionSummaryDto body
    ) {
        formResponseService.deleteResponses(formId, body);
    }

    @GetMapping(path = "{formId}/all-response-count-and-ids")
    @Cacheable(cacheNames = {"responseCountAndIds"}, key = "#formId")
    public AllResponseCountAndIdsResDto allResponseCountAndIds(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getAllResponseCountAndIds(formId);
    }

}
