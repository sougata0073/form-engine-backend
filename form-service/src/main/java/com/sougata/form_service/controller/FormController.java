package com.sougata.form_service.controller;

import com.sougata.form_service.constant.cacheNames.FormCacheNames;
import com.sougata.form_service.constant.cacheNames.QuestionCacheNames;
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

    // Cache OK
    @PostMapping
    public ResponseEntity<FormInfoResDto> addForm(
            @Valid @RequestBody FormAddUpdateReqDto req,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        var res = formService.createForm(req, userId);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    // Cache OK
    @PostMapping(path = "{formId}/copy")
    public ResponseEntity<FormInfoResDto> copyForm(
            @Valid @RequestBody CopyFormReqDto req,
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        var res = formService.copyForm(formId, req, userId);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    // Cache OK
    @DeleteMapping(path = "{formId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {FormCacheNames.FORM_INFO, FormCacheNames.FORM_QUESTION_IDS}, key = "#formId"),
    })
    public SuccessMessageDto deleteForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.deleteForm(formId, userId);
    }

    // Cache OK
    @PutMapping(path = "{formId}")
    public FormInfoResDto updateForm(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormAddUpdateReqDto dto,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.updateForm(formId, dto, userId);
    }

    // Cache OK
    @PatchMapping(path = "{formId}/rename")
    public SuccessMessageDto renameForm(
            @PathVariable("formId") UUID formId,
            @RequestBody FormRenameReqDto body,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.renameForm(formId, body, userId);
    }

    // Cache OK
    @GetMapping(path = "{formId}")
    public FormResponseDto getForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.getForm(formId, userId);
    }

    // Cache OK
    @GetMapping(path = "{formId}/details")
    public FormResponseDto getFormDetails(@PathVariable("formId") UUID formId) {
        return formServiceCached.getFormDetails(formId);
    }

    // Cache OK
    @GetMapping(path = "{formId}/view")
    public FormResponseDto viewForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.viewForm(formId, userId);
    }

    // Cache OK
    @GetMapping(path = "{formId}/info")
    @Cacheable(cacheNames = {FormCacheNames.FORM_INFO}, key = "#formId")
    public FormInfoResDto getFormInfo(@PathVariable("formId") UUID formId) {
        return formService.getFormInfo(formId);
    }

    // Cache OK
    @GetMapping(path = "{formId}/question-summaries")
    @Cacheable(cacheNames = {QuestionCacheNames.QUESTION_SUMMARIES}, key = "#formId")
    public QuestionSummariesResDto getQuestionSummaries(@PathVariable("formId") UUID formId) {
        return questionService.getQuestionSummaries(formId);
    }

    // Cache OK
    @GetMapping(path = "{formId}/questions/{questionId}/summary")
    @Cacheable(cacheNames = {QuestionCacheNames.QUESTION_SUMMARY}, key = "#questionId")
    public QuestionSummaryDto getQuestionSummary(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestionSummary(formId, questionId);
    }

    // Cache OK
    @GetMapping(path = "{formId}/questions/{questionId}")
    @Cacheable(cacheNames = {QuestionCacheNames.QUESTION_DETAILS}, key = "#questionId")
    public QuestionRes getQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestion(formId, questionId);
    }

    // Cache OK
    @PostMapping(path = "{formId}/questions")
    public ResponseEntity<QuestionRes> addQuestion(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        var res = questionService.createQuestion(formId, body);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    // Cache OK
    @PutMapping(path = "{formId}/questions/{questionId}")
    public QuestionRes updateQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        return questionService.updateQuestion(formId, questionId, body);
    }

    // Cache OK
    @DeleteMapping(path = "{formId}/questions/{questionId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {QuestionCacheNames.QUESTION_DETAILS, QuestionCacheNames.QUESTION_SUMMARY}, key = "#questionId")
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

    // Cache fix pending
    @PatchMapping(path = "{formId}/questions/{questionId}/order")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"questionSummaries"}, key = "#formId"),
            @CacheEvict(cacheNames = {"questionDetails", "questionSummary"}, key = "#questionId")
    })
    public SuccessMessageDto updateQuestionIndex(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid QuestionOrderUpdateReqDto req
    ) {
        return questionService.updateOrderIndex(formId, questionId, req);
    }
}
