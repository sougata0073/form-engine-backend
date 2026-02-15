package com.sougata.form_service.controller;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.FormAddUpdateReqDto;
import com.sougata.form_service.dto.form.FormAddUpdateResDto;
import com.sougata.form_service.dto.form.FormResponseDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.dto.validation.request.ResponseValidationRequestDto;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<FormAddUpdateResDto> addForm(@Valid @RequestBody FormAddUpdateReqDto dto) {
        var res = formService.createForm(dto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping(path = "{formId}")
    public FormAddUpdateResDto updateForm(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormAddUpdateReqDto dto
    ) {
        return formService.updateForm(formId, dto);
    }

    @GetMapping(path = "{formId}")
    public FormResponseDto getForm(@PathVariable("formId") UUID formId) {
        return formService.getForm(formId);
    }

    @GetMapping(path = "{formId}/view")
    public FormResponseDto viewForm(@PathVariable("formId") UUID formId) {
        return formService.viewForm(formId);
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

    @DeleteMapping(path = "{formId}/questions/{questionId}", params = "questionType")
    public SuccessMessageDto deleteQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId,
            @RequestParam("questionType") QuestionType questionType
    ) {
        return questionService.deleteQuestion(formId, questionId, questionType);
    }

    @PostMapping(path = "{formId}/validate-response")
    public SuccessMessageDto validateResponse(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody ResponseValidationRequestDto body
    ) {
        return formService.validateResponse(formId, body);
    }
}
