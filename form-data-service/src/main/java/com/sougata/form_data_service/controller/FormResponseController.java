package com.sougata.form_data_service.controller;

import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
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
            @CacheEvict(cacheNames = {"formResponseSummaryShort", "responseSummaries"}, key = "#formId"),
            @CacheEvict(
                    cacheNames = {"responseByQuestionSummary", "responseByQuestion", "formResponseSummaries", "responseSummary"},
                    allEntries = true
            )
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
    @Cacheable(cacheNames = {"formResponseSummaryShort"}, key = "#formId")
    public FormResponseSummaryShortDto getFormResponseSummary(
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
    @Cacheable(
            cacheNames = {"responseSummary"},
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

    @GetMapping(path = "{formId}/questions/{questionId}/form-response-summaries", params = {"formResponsesIdentifier"})
    @Cacheable(
            cacheNames = {"formResponseSummaries"},
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

    @GetMapping(path = "{formId}/response/{formResponseId}")
    @Cacheable(
            cacheNames = {"individualFormResponse"},
            key = "{#formId, #formResponseId}"
    )
    public ResponseIndividualResDto getIndividualFormResponse(
            @PathVariable("formId") UUID formId,
            @PathVariable("formResponseId") Long formResponseId
    ) {
        return formResponseService.getIndividualFormResponse(formId, formResponseId);
    }

    // Page number starts from 0
    @GetMapping(path = "{formId}/response-by-page/{page}")
    @Cacheable(
            cacheNames = {"individualFormResponseByPage"},
            key = "{#formId, #page}"
    )
    public ResponseIndividualResDto getIndividualFormResponseByPage(
            @PathVariable("formId") UUID formId,
            @PathVariable("page") Long page
    ) {
        return formResponseService.getIndividualFormResponseByOPage(formId, page);
    }

    @GetMapping(path = "{formId}/is-response-already-submitted", params = "userId")
    // TODO: Enable caching when delete form response endpoint is created
    public boolean getIsResponseAlreadySubmitted(
            @PathVariable("formId") UUID formId,
            @RequestParam("userId") UUID userId
    ) {
        return formResponseService.getIsResponseAlreadySubmitted(formId, userId);
    }

    @DeleteMapping(path = "{formId}/questions/{questionId}/responses")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formResponseSummaryShort", "responseSummaries"}, key = "#formId"),
            @CacheEvict(
                    cacheNames = {"responseByQuestionSummary", "responseByQuestion", "formResponseSummaries", "responseSummary", "individualFormResponse"},
                    allEntries = true
            )
    })
    public void deleteQuestionResponses(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        formResponseService.deleteResponses(formId, questionId);
    }

}
