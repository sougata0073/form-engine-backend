package com.sougata.form_data_service.service.impl;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.dto.form.FormResponseAddResDto;
import com.sougata.form_data_service.dto.form.FormResponseSummaryResDto;
import com.sougata.form_data_service.dto.response.FormResponseSummaryDto;
import com.sougata.form_data_service.dto.response.question.AllResponseCountAndIdsResDto;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryResDto;
import com.sougata.form_data_service.dto.user.UserSummaryDto;
import com.sougata.form_data_service.dto.validation.ResponseValidationRequestDto;
import com.sougata.form_data_service.exception.FormSubmitException;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.feignClient.FormServiceFeignClient;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.service.FormResponseService;
import com.sougata.form_data_service.service.responseManager.ResponseManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class FormResponseServiceImpl implements FormResponseService {

    private final FormResponseRepository formResponseRepository;
    private final ResponseManagerFactory responseManagerFactory;
    private final AuthServiceFeignClient authServiceFeignClient;
    private final FormServiceFeignClient formServiceFeignClient;

    @Autowired
    public FormResponseServiceImpl(
            FormResponseRepository formResponseRepository,
            ResponseManagerFactory responseManagerFactory,
            AuthServiceFeignClient authServiceFeignClient, FormServiceFeignClient formServiceFeignClient
    ) {
        this.formResponseRepository = formResponseRepository;
        this.responseManagerFactory = responseManagerFactory;
        this.authServiceFeignClient = authServiceFeignClient;
        this.formServiceFeignClient = formServiceFeignClient;
    }

    @Override
    @Transactional
    public FormResponseAddResDto saveResponse(UUID formId, FormResponseAddReqDto req, UUID userId) {

        if (formResponseRepository.existsByFormIdAndUserId(formId, userId)) {
            throw new FormSubmitException("Form Response already submitted for User ID: " + userId);
        }

        var formInfo = formServiceFeignClient.getFormInfo(formId);

        if (!formInfo.published()) {
            throw new FormSubmitException("This form is not published yet. FOrm ID: " + formId);
        }

        boolean isAcceptingDateExceeded =
                formInfo.stopAcceptingResponseOn() != null &&
                        Instant.now().isAfter(formInfo.stopAcceptingResponseOn());

        boolean isNumberOfResponseExceeded = formInfo.stopAcceptingResponseAfterResponse() != null &&
                getFormResponseSummary(formId).responseCount() >= Integer.toUnsignedLong(formInfo.stopAcceptingResponseAfterResponse());

        if (!formInfo.acceptingResponse() || isAcceptingDateExceeded || isNumberOfResponseExceeded) {
            throw new FormSubmitException("This form is not accepting response. Form ID: " + formId);
        }

        var validationBody = new ResponseValidationRequestDto(req.responses());

        var validationResponse = formServiceFeignClient.validateResponse(formId, validationBody);

        FormResponse formResponse = new FormResponse();

        formResponse.setFormId(formId);
        formResponse.setUserId(userId);

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

    @Override
    public AllResponseCountAndIdsResDto getAllResponseCountAndIds(UUID formId) {
        var resCountAndIds = formResponseRepository.getAllResponseCountAndIds(formId);

        if (resCountAndIds == null) {
            return new AllResponseCountAndIdsResDto(0L, Collections.emptyList());
        }

        var responseIds = Arrays.asList(resCountAndIds.get("responseIds", Long[].class));
        var userIds = Arrays.asList(resCountAndIds.get("userIds", UUID[].class));

        var users = authServiceFeignClient.userSummaries(userIds);

        var responseIdUserIdMap = new HashMap<Long, UUID>();

        for (int i = 0; i < responseIds.size(); i++) {
            responseIdUserIdMap.put(responseIds.get(i), userIds.get(i));
        }

        // TODO
        var responses = responseIds.stream().map(resId -> {
            var userId = responseIdUserIdMap.get(resId);
            var user = users.users().stream().filter(u -> u.userId().equals(userId))
                    .findFirst()
                    .orElse(new UserSummaryDto(UUID.randomUUID(), "User name", "Email", "https://picsum.photos/200/300"));
//                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            return new FormResponseSummaryDto(resId, user.userId(), user.userName(), user.email(), user.avatarUrl());
        }).toList();

        return new AllResponseCountAndIdsResDto(
                resCountAndIds.get("totalResponseCount", Long.class), responses
        );
    }

}
