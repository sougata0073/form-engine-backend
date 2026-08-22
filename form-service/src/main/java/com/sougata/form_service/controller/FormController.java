package com.sougata.form_service.controller;

import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.*;
import com.sougata.form_service.dto.question.QuestionSummariesResDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.request.QuestionOrderUpdateReqDto;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.service.formSchema.FormService;
import com.sougata.form_service.service.formSchema.FormServiceCached;
import com.sougata.form_service.service.formSchema.QuestionService;
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
    public ResponseEntity<FormInfoResDto> addForm(
            @Valid @RequestBody FormAddUpdateReqDto req,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        var res = formService.createForm(req, userId);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping(path = "{formId}/copy")
    public ResponseEntity<FormInfoResDto> copyForm(
            @Valid @RequestBody CopyFormReqDto req,
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        var res = formService.copyForm(formId, req, userId);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "{formId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"formDetails", "formInfo"}, key = "#formId"),
    })
    public SuccessMessageDto deleteForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.deleteForm(formId, userId);
    }

    @PutMapping(path = "{formId}")
    public FormInfoResDto updateForm(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormAddUpdateReqDto dto,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.updateForm(formId, dto, userId);
    }

    @PatchMapping(path = "{formId}/rename")
    public SuccessMessageDto renameForm(
            @PathVariable("formId") UUID formId,
            @RequestBody FormRenameReqDto body,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.renameForm(formId, body, userId);
    }

    @GetMapping(path = "{formId}")
    public FormResponseDto getForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.getForm(formId, userId);
    }

    @GetMapping(path = "{formId}/details")
    public FormResponseDto getFormDetails(@PathVariable("formId") UUID formId) {
        return formServiceCached.getFormDetails(formId);
    }

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
    @Cacheable(cacheNames = {"questionSummary"}, key = "#questionId")
    public QuestionSummaryDto getQuestionSummary(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestionSummary(formId, questionId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}")
    @Cacheable(cacheNames = {"questionDetails"}, key = "#questionId")
    public QuestionRes getQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestion(formId, questionId);
    }

    @PostMapping(path = "{formId}/questions")
    public ResponseEntity<QuestionRes> addQuestion(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        var res = questionService.createQuestion(formId, body);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping(path = "{formId}/questions/{questionId}")
    public QuestionRes updateQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        return questionService.updateQuestion(formId, questionId, body);
    }

    @DeleteMapping(path = "{formId}/questions/{questionId}")
    @Caching(evict = {
            @CacheEvict(cacheNames = {"questionDetails", "questionSummary"}, key = "#questionId")
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
            @CacheEvict(cacheNames = {"questionSummary", "questionDetails"}, key = "#questionId")
    })
    public SuccessMessageDto updateQuestionIndex(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @RequestBody @Valid QuestionOrderUpdateReqDto req
    ) {
        return questionService.updateOrderIndex(formId, questionId, req);
    }
}
