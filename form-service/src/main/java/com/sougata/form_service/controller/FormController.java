package com.sougata.form_service.controller;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.*;
import com.sougata.form_service.dto.question.QuestionSummariesResDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.dto.validation.request.ResponseValidationRequestDto;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/forms")
@CrossOrigin
public class FormController {

    private final FormService formService;
    private final QuestionService questionService;

    @Autowired
    public FormController(FormService formService, QuestionService questionService) {
        this.formService = formService;
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<FormInfoResDto> addForm(
            @Valid @RequestBody FormAddUpdateReqDto dto,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        var res = formService.createForm(dto, userId);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "{formId}")
    @CacheEvict(cacheNames = {"formDetails", "formInfo"}, key = "#formId")
    public SuccessMessageDto deleteForm(@PathVariable("formId") UUID formId) {
        return formService.deleteForm(formId);
    }

    @PutMapping(path = "{formId}")
    @CacheEvict(cacheNames = {"formDetails", "formInfo"}, key = "#formId")
    public FormInfoResDto updateForm(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormAddUpdateReqDto dto,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.updateForm(formId, dto, userId);
    }

    @GetMapping(path = "{formId}")
    public FormResponseDto getForm(@PathVariable("formId") UUID formId) {
        return formService.getForm(formId);
    }

    @GetMapping(path = "{formId}/details")
    public FormResponseDto getFormDetails(@PathVariable("formId") UUID formId) {
        return formService.getFormDetails(formId);
    }

    @GetMapping(path = "{formId}/info")
    public FormInfoResDto getFormInfo(@PathVariable("formId") UUID formId) {
        return formService.getFormInfo(formId);
    }

    @PatchMapping(path = "{formId}/rename")
    @CacheEvict(cacheNames = {"formDetails", "formInfo"}, key = "#formId")
    public SuccessMessageDto renameForm(@PathVariable("formId") UUID formId, @RequestBody FormRenameReqDto body) {
        return formService.renameForm(formId, body);
    }

    @GetMapping(path = "{formId}/view")
    public FormResponseDto viewForm(
            @PathVariable("formId") UUID formId,
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.viewForm(formId, userId);
    }

    @GetMapping(path = "{formId}/question-summaries")
    public QuestionSummariesResDto getQuestionSummaries(@PathVariable("formId") UUID formId) {
        return questionService.getQuestionSummaries(formId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/summary")
    public QuestionSummaryDto getQuestionSummary(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestionSummary(formId, questionId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}")
    public QuestionRes getQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.getQuestion(formId, questionId);
    }

    @PostMapping(path = "{formId}/questions")
    @CacheEvict(cacheNames = {"formDetails"}, key = "#formId")
    public ResponseEntity<QuestionRes> addQuestion(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        var res = questionService.createQuestion(formId, body);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping(path = "{formId}/questions/{questionId}")
    @CacheEvict(cacheNames = {"formDetails"}, key = "#formId")
    public QuestionRes updateQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @Valid @RequestBody QuestionAddUpdateReq body
    ) {
        return questionService.updateQuestion(formId, questionId, body);
    }

    @DeleteMapping(path = "{formId}/questions/{questionId}")
    @CacheEvict(cacheNames = {"formDetails"}, key = "#formId")
    public SuccessMessageDto deleteQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return questionService.deleteQuestion(formId, questionId);
    }

    @PostMapping(path = "{formId}/validate-response")
    public SuccessMessageDto validateResponse(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody ResponseValidationRequestDto body
    ) {
        return formService.validateResponse(formId, body);
    }

    @GetMapping(path = "recent")
    public List<FormSummaryResDto> getRecentForms(
            @RequestHeader("auth-jwt") UUID userId
    ) {
        return formService.getFormsSummaries(userId);
    }
}
