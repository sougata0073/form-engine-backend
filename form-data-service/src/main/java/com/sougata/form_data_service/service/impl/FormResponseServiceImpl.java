package com.sougata.form_data_service.service.impl;

import com.sougata.form_data_service.dto.common.SuccessMessageDto;
import com.sougata.form_data_service.dto.form.*;
import com.sougata.form_data_service.dto.question.response.QuestionRes;
import com.sougata.form_data_service.dto.response.individual.ResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.ResponseIndividualResDto;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionResponse;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionSummary;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import com.sougata.form_data_service.dto.user.UserSummaryShortDto;
import com.sougata.form_data_service.dto.validation.ResponseValidationRequestDto;
import com.sougata.form_data_service.exception.FormSubmitException;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.feignClient.FormServiceFeignClient;
import com.sougata.form_data_service.formValidation.service.FormSchemaService;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.QuestionResponse;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.service.FormResponseService;
import com.sougata.form_data_service.service.responseManager.ResponseManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FormResponseServiceImpl implements FormResponseService {

    private final FormResponseRepository formResponseRepository;
    private final ResponseManagerFactory responseManagerFactory;
    private final FormServiceFeignClient formServiceFeignClient;
    private final QuestionResponseRepository questionResponseRepository;
    private final FormSchemaService formSchemaService;
    private final AuthServiceFeignClient authServiceFeignClient;

    @Autowired
    public FormResponseServiceImpl(
            FormResponseRepository formResponseRepository,
            ResponseManagerFactory responseManagerFactory,
            FormServiceFeignClient formServiceFeignClient,
            QuestionResponseRepository questionResponseRepository,
            FormSchemaService formSchemaService,
            AuthServiceFeignClient authServiceFeignClient
    ) {
        this.formResponseRepository = formResponseRepository;
        this.responseManagerFactory = responseManagerFactory;
        this.formServiceFeignClient = formServiceFeignClient;
        this.questionResponseRepository = questionResponseRepository;
        this.formSchemaService = formSchemaService;
        this.authServiceFeignClient = authServiceFeignClient;
    }

    @Override
    @Transactional
    public FormResponseAddResDto saveResponse(UUID formId, FormResponseAddReqDto req, UUID userId) {

        if (formResponseRepository.existsByFormIdAndUserId(formId, userId)) {
            throw new FormSubmitException("Form Response already submitted for User ID: " + userId);
        }

        var formInfo = formServiceFeignClient.getFormInfo(formId);

        if (!formInfo.getPublished()) {
            throw new FormSubmitException("This form is not published yet. Form ID: " + formId);
        }

        boolean isAcceptingDateExceeded =
                formInfo.getStopAcceptingResponseOn() != null &&
                        Instant.now().isAfter(formInfo.getStopAcceptingResponseOn());

        boolean isNumberOfResponseExceeded = formInfo.getStopAcceptingResponseAfterResponse() != null &&
                getFormResponseSummaryShort(formId).getResponseCount() >= Integer.toUnsignedLong(formInfo.getStopAcceptingResponseAfterResponse());

        if (!formInfo.getAcceptingResponse() || isAcceptingDateExceeded || isNumberOfResponseExceeded) {
            throw new FormSubmitException("This form is not accepting response. Form ID: " + formId);
        }

        var validationBody = new ResponseValidationRequestDto(req.getResponses());

        var validationResponse = formSchemaService.validateResponse(formId, validationBody);

        FormResponse formResponse = new FormResponse();

        formResponse.setFormId(formId);
        formResponse.setUserId(userId);

        var savedFormResponse = formResponseRepository.save(formResponse);

        req.getResponses().forEach(response -> {
            var responseManager = responseManagerFactory.get(
                    response.getQuestionType()
            );
            responseManager.create(response, savedFormResponse);
        });

        return new FormResponseAddResDto(savedFormResponse.getId());
    }

    @Override
    public FormResponseSummaryShortDto getFormResponseSummaryShort(UUID formId) {
        return formResponseRepository.getFormResponseSummary(formId);
    }

    @Override
    public ResponseSummaryResDto getResponseSummaries(UUID formId) {

        var questions = formServiceFeignClient.getFormDetails(formId).getQuestions();

        var questionTypeMap = questions.stream().collect(Collectors.groupingBy(QuestionRes::getQuestionType));

        var result = new ArrayList<ResponseSummaryDto<?>>();

        questionTypeMap.keySet().forEach(qType -> {
            var filteredQuestions = questionTypeMap.get(qType);

            var manager = responseManagerFactory.get(qType);
            var summaries = manager.getResponseSummaries(formId, filteredQuestions);

            result.addAll(summaries);
        });

        result.sort(Comparator.comparingInt(ResponseSummaryDto::getOrderIndex));

        return new ResponseSummaryResDto(result);
    }

    @Override
    public ResponseSummaryDto<?> getResponseSummary(UUID formId, Long questionId, Pageable pageable) {
        var question = formServiceFeignClient.getQuestion(formId, questionId);
        var manager = responseManagerFactory.get(question.getQuestionType());

        return manager.getResponseSummary(formId, questionId, question, pageable);
    }

    @Override
    public ResponseByQuestionSummary getResponseByQuestionSummary(UUID formId, Long questionId) {
        var qRes = formServiceFeignClient.getQuestion(formId, questionId);
        var manager = responseManagerFactory.get(qRes.getQuestionType());

        var res = manager.getResponseByQuestionSummary(formId, qRes);

        res.setQuestionId(qRes.getId());
        res.setQuestion(qRes.getQuestion());
        res.setQuestionType(qRes.getQuestionType());

        return res;
    }

    @Override
    public FormResponseSummariesDto getFormResponseSummaries(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        var questionSummary = formServiceFeignClient.getQuestionSummary(formId, questionId);
        var manager = responseManagerFactory.get(questionSummary.getQuestionType());

        var resAndUserIds = manager.getFormResponseAndUserIds(formId, questionId, formResponsesIdentifier, pageable);

        var userIds = resAndUserIds.stream().map(tuple -> tuple.get("userId", UUID.class)).toList();

        var userSummaries = authServiceFeignClient.userSummaries(userIds).getUsers();

        var userSummariesMap = new HashMap<UUID, UserSummaryShortDto>();
        userSummaries.forEach(userSummary -> userSummariesMap.put(userSummary.getId(), userSummary));

        var formResponseSummaries = new ArrayList<FormResponseSummaryDto>();

        resAndUserIds.forEach(tuple -> {
            var resId = tuple.get("responseId", Long.class);
            var userId = tuple.get("userId", UUID.class);

            var user = Optional.ofNullable(userSummariesMap.get(userId)).orElse(new UserSummaryShortDto(null, null));

            formResponseSummaries.add(
                    new FormResponseSummaryDto(
                            resId,
                            user.getId(),
                            user.getUserName()
                    )
            );
        });

        return new FormResponseSummariesDto(formResponseSummaries);
    }

    @Override
    public ResponseIndividualResDto getIndividualFormResponse(UUID formId, Long formResponseId) {
        var formResponse = formResponseRepository.findByFormIdAndId(formId, formResponseId)
                .orElseThrow(() -> new IllegalArgumentException("Form response not found with ID: " + formResponseId));

        var questionTypeMap = formResponse.getQuestionResponses().stream().collect(Collectors.groupingBy(QuestionResponse::getQuestionType));

        var result = new ArrayList<ResponseIndividualDto>();

        questionTypeMap.keySet().forEach(qType -> {
            var manager = responseManagerFactory.get(qType);

            var indiResponses = manager.getIndividualResponses(formId, formResponseId);

            result.addAll(indiResponses);
        });

        var formResponsePage = formResponseRepository.getPageNumberOfFormResponse(formId, formResponseId)
                .orElseThrow(() -> new IllegalArgumentException("Form response not found with ID: " + formResponseId));

        return new ResponseIndividualResDto(formResponseId, formResponsePage, formResponse.getUserId(), result);
    }

    @Override
    public ResponseIndividualResDto getIndividualFormResponseByOPage(UUID formId, Long page) {
        var formResponseId = formResponseRepository.getFormResponseIdFromPage(formId, page)
                .orElseThrow(() -> new IllegalArgumentException("Form response not found for page: " + page));

        return getIndividualFormResponse(formId, formResponseId);
    }

    @Override
    public ResponseQuestionDto<? extends ResponseByQuestionResponse> getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var qSummary = formServiceFeignClient.getQuestionSummary(formId, questionId);
        var manager = responseManagerFactory.get(qSummary.getQuestionType());

        return manager.getResponseByQuestion(formId, qSummary.getId(), extraParams, pageable);
    }

    @Override
    public boolean getIsResponseAlreadySubmitted(UUID formId, UUID userId) {
        return formResponseRepository.existsByFormIdAndUserId(formId, userId);
    }

    @Override
    public SuccessMessageDto deleteFormResponse(UUID formId, UUID userId, Long formResponseId) {
        formResponseRepository.deleteByFormResponseId(formResponseId);

        return SuccessMessageDto.create("Response deleted successfully. Form id: " + formId + " Form response ID: " + formResponseId);
    }

    @Override
    public void deleteQuestionResponses(UUID formId, Long questionId) {
        questionResponseRepository.deleteAllByQuestionId(questionId);
    }
}
