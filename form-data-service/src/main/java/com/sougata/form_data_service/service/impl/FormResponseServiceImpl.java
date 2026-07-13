package com.sougata.form_data_service.service.impl;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryResDto;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import com.sougata.form_data_service.dto.validation.request.ResponseValidationRequestDto;
import com.sougata.form_data_service.exception.FormResponseAlreadySubmittedException;
import com.sougata.form_data_service.feignClient.FormServiceFeignClient;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.service.FormResponseService;
import com.sougata.form_data_service.service.responseManager.ResponseManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

@Service
public class FormResponseServiceImpl implements FormResponseService {

    private final FormResponseRepository formResponseRepository;
    private final ResponseManagerFactory responseManagerFactory;
    private final FormServiceFeignClient formServiceFeignClient;

    @Autowired
    public FormResponseServiceImpl(FormResponseRepository formResponseRepository, ResponseManagerFactory responseManagerFactory, FormServiceFeignClient formServiceFeignClient) {
        this.formResponseRepository = formResponseRepository;
        this.responseManagerFactory = responseManagerFactory;
        this.formServiceFeignClient = formServiceFeignClient;
    }

    @Transactional
    @Override
    public FormResponseAddResDto saveResponse(UUID formId, FormResponseAddReqDto req, UUID userId) {

        if (formResponseRepository.existsByFormIdAndUserId(formId, userId)) {
            throw new FormResponseAlreadySubmittedException("Form Response already submitted for User ID: " + userId);
        }

        FormResponse formResponse = new FormResponse();

        formResponse.setFormId(formId);
        formResponse.setUserId(userId);

        var validationBody = new ResponseValidationRequestDto(req.responses());

//        formServiceFeignClient.validateResponse(formId, validationBody);

        var savedFormResponse = formResponseRepository.save(formResponse);

        req.responses().forEach(response -> {
            var responseManager = responseManagerFactory.get(
                    response.getQuestionType()
            );
            responseManager.create(response, savedFormResponse);
        });

        return new FormResponseAddResDto(savedFormResponse.getId());
    }

    @Override
    public FormResponseSummaryResDto getFormResponseSummary(UUID formId) {
        return formResponseRepository.getFormResponseSummary(formId);
    }

    @Override
    public ResponseSummaryResDto getResponseSummaries(UUID formId) {

        var questions = formServiceFeignClient.getFormDetails(formId).questions();

        var result = new ArrayList<ResponseSummaryDto>();

        responseManagerFactory.getAll().forEach(manager -> {
            var filteredQuestions = questions.stream().filter(q ->
                    q.getQuestionType().name().equals(manager.getQuestionType().name())
            ).toList();

            var summaries = manager.getResponseSummaries(formId, filteredQuestions);

            result.addAll(summaries);
        });

        result.sort(Comparator.comparingInt(ResponseSummaryDto::getOrderIndex));

        return new ResponseSummaryResDto(result);
    }

    @Override
    public ResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId) {
        var qRes = formServiceFeignClient.getQuestion(formId, questionId);
        var manager = responseManagerFactory.get(qRes.getQuestionType());

        return manager.getResponseByQuestion(formId, qRes);
    }

    @Override
    public boolean getIsResponseAlreadySubmitted(UUID formId, UUID userId) {
        return formResponseRepository.existsByFormIdAndUserId(formId, userId);
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId, QuestionType questionType) {
        responseManagerFactory.get(questionType).deleteResponses(formId, questionId);
    }

}
