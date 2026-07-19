package com.sougata.form_data_service.controller;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryResDto;
import com.sougata.form_data_service.dto.response.question.AllResponseCountAndIdsResDto;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import com.sougata.form_data_service.service.FormResponseService;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<FormResponseAddResDto> addFormResponse(
            @PathVariable("formId") UUID formId,
            @Valid @RequestBody FormResponseAddReqDto dto,
            @RequestHeader("auth-jwt") UUID authJwt
    ) {
        var res = formResponseService.saveResponse(formId, dto, authJwt);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping(path = "{formId}/response-summary")
    public FormResponseSummaryResDto getFormResponseSummary(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getFormResponseSummary(formId);
    }

    @GetMapping(path = "{formId}/response-summaries")
    public ResponseSummaryResDto getResponseSummaries(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getResponseSummaries(formId);
    }

    @GetMapping(path = "{formId}/questions/{questionId}/response")
    public ResponseQuestionDto getResponseByQuestion(
            @PathVariable("formId") UUID formId,
            @PathVariable("questionId") Long questionId
    ) {
        return formResponseService.getResponseByQuestion(formId, questionId);
    }

    @GetMapping(path = "{formId}/is-response-already-submitted", params = "userId")
    public boolean getIsResponseAlreadySubmitted(
            @PathVariable("formId") UUID formId,
            @RequestParam("userId") UUID userId
    ) {
        return formResponseService.getIsResponseAlreadySubmitted(formId, userId);
    }

    @DeleteMapping(path = "{formId}", params = {"questionId", "questionType"})
    public void deleteResponses(
            @PathVariable("formId") UUID formId,
            @RequestParam("questionId") Long questionId,
            @RequestParam("questionType") QuestionType questionType
    ) {
        formResponseService.deleteResponses(formId, questionId, questionType);
    }

    @GetMapping(path = "{formId}/all-response-count-and-ids")
    public AllResponseCountAndIdsResDto allResponseCountAndIds(
            @PathVariable("formId") UUID formId
    ) {
        return formResponseService.getAllResponseCountAndIds(formId);
    }

}
