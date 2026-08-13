package com.sougata.form_service.controller;

import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.*;
import com.sougata.form_service.dto.question.QuestionSummariesResDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.request.QuestionOrderUpdateReqDto;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.FormServiceCached;
import com.sougata.form_service.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/forms")
@CrossOrigin
public class FormController {

    private final FormService formService;
    private final FormServiceCached formServiceCached;
    private final QuestionService questionService;

    @Autowired
    public FormController(FormService formService, FormServiceCached formServiceCached, QuestionService questionService) {
        this.formService = formService;
        this.formServiceCached = formServiceCached;
        this.questionService = questionService;
    }

    @PostMapping
    @CacheEvict(cacheNames = "recentForms", key = "{#userId}")
    public ResponseEntity<FormInfoResDto> addForm(
            @Valid @RequestBody FormAddUpdateReqDto dto,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        var res = formService.createForm(dto, userId);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "{formId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formDetails", "formInfo"}, key = "#formId"),
            @CacheEvict(cacheNames = "recentForms", key = "{#userId}")
    })
    public SuccessMessageDto deleteForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.deleteForm(formId);
    }

    @PutMapping(path = "{formId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formDetails", "formInfo"}, key = "#formId"),
            @CacheEvict(cacheNames = "recentForms", key = "{#userId}")
    })
    public FormInfoResDto updateForm(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormAddUpdateReqDto dto,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.updateForm(formId, dto, userId);
    }

    @PatchMapping(path = "{formId}/rename")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formDetails", "formInfo"}, key = "#formId"),
            @CacheEvict(cacheNames = "recentForms", key = "{#userId}")
    })
    public SuccessMessageDto renameForm(
            @PathVariable("formId") UUID formId,
            @RequestBody FormRenameReqDto body,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.renameForm(formId, body);
    }

    // Cached in service
    @GetMapping(path = "{formId}")
    @CacheEvict(cacheNames = {"recentForms"}, key = "#userId")
    public FormResponseDto getForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.getForm(formId);
    }

    // Cached in service
    @GetMapping(path = "{formId}/details")
    public FormResponseDto getFormDetails(@PathVariable("formId") UUID formId) {
        return formServiceCached.getFormDetails(formId);
    }

    // Cached in service
    @GetMapping(path = "{formId}/view")
    public FormResponseDto viewForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.viewForm(formId, userId);
    }

    @GetMapping(path = "{formId}/info")
    @Cacheable(cacheNames = {"formInfo"}, key = "#formId")
    public FormInfoResDto getFormInfo(@PathVariable("formId") UUID formId) {
        return formService.getFormInfo(formId);
    }

    @GetMapping(path = "{formId}/question-summaries")
    @Cacheable(cacheNames = {"questionSummaries"}, key = "#formId")
    public QuestionSummariesResDto getQuestionSummaries(@PathVariable("formId") UUID formId) {
        return questionService.getQuestionSummaries(formId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/summary")
    @Cacheable(cacheNames = {"questionSummary"}, key = "{#formId, #questionId}")
    public QuestionSummaryDto getQuestionSummary(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestionSummary(formId, questionId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}")
    @Cacheable(cacheNames = {"questionDetails"}, key = "{#formId, #questionId}")
    public QuestionRes getQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestion(formId, questionId);
    }

    @PostMapping(path = "{formId}/questions")
    @CacheEvict(cacheNames = {"formDetails", "questionSummaries"}, key = "#formId")
    public ResponseEntity<QuestionRes> addQuestion(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        var res = questionService.createQuestion(formId, body);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping(path = "{formId}/questions/{questionId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formDetails", "questionSummaries"}, key = "#formId"),
            @CacheEvict(cacheNames = {"questionDetails", "questionSummary"}, key = "{#formId, #questionId}")
    })
    public QuestionRes updateQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        return questionService.updateQuestion(formId, questionId, body);
    }

    @DeleteMapping(path = "{formId}/questions/{questionId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formDetails", "questionSummaries"}, key = "#formId"),
            @CacheEvict(cacheNames = {"questionDetails", "questionSummary"}, key = "{#formId, #questionId}")
    })
    public SuccessMessageDto deleteQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.deleteQuestion(formId, questionId);
    }

    @GetMapping(path = "recent")
    @Cacheable(cacheNames = {"recentForms"}, key = "#userId")
    public FormSummariesRes getRecentForms(
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.getFormsSummaries(userId);
    }

    @PatchMapping(path = "{formId}/questions/{questionId}/order")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formDetails", "questionSummaries"}, key = "#formId"),
            @CacheEvict(cacheNames = {"questionSummary", "questionDetails"}, key = "{#formId, #questionId}")
    })
    public SuccessMessageDto updateQuestionIndex(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid QuestionOrderUpdateReqDto req
    ) {
        return questionService.updateOrderIndex(formId, questionId, req);
    }
}
