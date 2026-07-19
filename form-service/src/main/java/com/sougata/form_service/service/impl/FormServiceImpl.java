package com.sougata.form_service.service.impl;

import com.sougata.form_service.constant.CommonMessages;
import com.sougata.form_service.constant.ValidationMessages;
import com.sougata.form_service.constant.ViewFormErrorReason;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.*;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.dto.validation.request.ResponseValidationRequestDto;
import com.sougata.form_service.exception.*;
import com.sougata.form_service.feignClient.FormDataServiceFeignClient;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.projection.QuestionIdProjection;
import com.sougata.form_service.repository.FormRepository;
import com.sougata.form_service.repository.QuestionRepositoryFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepo;
    private final QuestionManagerFactory questionManagerFactory;
    private final QuestionRepositoryFactory questionRepositoryFactory;
    private final FormDataServiceFeignClient formDataServiceFeignClient;

    @Autowired
    public FormServiceImpl(
            FormRepository formRepo,
            QuestionManagerFactory questionManagerFactory,
            QuestionRepositoryFactory questionRepositoryFactory, FormDataServiceFeignClient formDataServiceFeignClient
    ) {
        this.formRepo = formRepo;
        this.questionManagerFactory = questionManagerFactory;
        this.questionRepositoryFactory = questionRepositoryFactory;
        this.formDataServiceFeignClient = formDataServiceFeignClient;
    }

    @Override
    public FormInfoResDto createForm(FormAddUpdateReqDto dto, UUID userId) {
        Form newForm = new Form();

        newForm.setUserId(userId);
        newForm.setName(dto.name());
        newForm.setTitle(dto.title());
        newForm.setDescription(dto.description());
        newForm.setPublished(dto.published());
        newForm.setAcceptingResponse(dto.acceptingResponse());
        newForm.setLastOpenedOn(Instant.now());

        Form savedForm = formRepo.save(newForm);

        return FormInfoResDto.create(savedForm);
    }

    @Override
    public FormInfoResDto updateForm(UUID formId, FormAddUpdateReqDto dto, UUID userId) {
        Form f = getFormById(formId);

        f.setUserId(userId);
        f.setName(dto.name());
        f.setTitle(dto.title());
        f.setDescription(dto.description());
        f.setPublished(dto.published());
        f.setAcceptingResponse(dto.acceptingResponse());
        f.setNotAcceptingResponseMessage(dto.notAcceptingResponseMessage());
        f.setStopAcceptingResponseOn(dto.stopAcceptingResponseOn());
        f.setStopAcceptingResponseAfterResponse(dto.stopAcceptingResponseAfterResponse());

        Form savedForm = formRepo.save(f);

        return FormInfoResDto.create(savedForm);
    }

    @Override
    public FormResponseDto getFormDetails(UUID id) {
        Form f = getFormById(id);

        List<QuestionRes> questions = new ArrayList<>();

        questionRepositoryFactory.getAll().forEach(qRepo ->
                questions.addAll(
                        qRepo.findByFormId(f.getId())
                                .stream()
                                .map(qRepo::toQuestionResDto)
                                .toList()
                ));

        questions.sort(Comparator.comparingInt(QuestionRes::getOrderIndex));

        return new FormResponseDto(
                f.getId(),
                f.getName(),
                f.getTitle(),
                f.getDescription(),
                f.getPublished(),
                f.getAcceptingResponse(),
                f.getNotAcceptingResponseMessage(),
                f.getStopAcceptingResponseOn(),
                f.getStopAcceptingResponseAfterResponse(),
                f.getLastOpenedOn(),
                questions
        );
    }

    @Override
    @Transactional
    public FormResponseDto getForm(UUID id) {

        formRepo.updateLastOpenedOn(id, Instant.now());

        return getFormDetails(id);
    }

    @Override
    public FormResponseDto viewForm(UUID id, UUID userId) {
        Form f = getFormById(id);

        if (!f.getPublished()) {
            throw new FormNotAcceptingResponseException(
                    new ViewFormErrorResDto(
                            null,
                            ViewFormErrorReason.NOT_PUBLISHED,
                            "We're sorry. This document is not published."
                    )
            );
        }

        boolean isAcceptingDateExceeded =
                f.getStopAcceptingResponseOn() != null &&
                        Instant.now().isAfter(f.getStopAcceptingResponseOn());

        boolean isNumberOfResponseExceeded = f.getStopAcceptingResponseAfterResponse() != null &&
                formDataServiceFeignClient.getFormResponseSummary(f.getId()).responseCount() >= f.getStopAcceptingResponseAfterResponse();

        if (!f.getAcceptingResponse() || isAcceptingDateExceeded || isNumberOfResponseExceeded) {
            throw new FormNotAcceptingResponseException(
                    new ViewFormErrorResDto(
                            f.getTitle(),
                            ViewFormErrorReason.NOT_ACCEPTING_RESPONSE,
                            f.getNotAcceptingResponseMessage()
                    )
            );
        }

        if (formDataServiceFeignClient.getIsResponseAlreadySubmitted(id, userId)) {
            throw new FormResponseAlreadySubmittedException(
                    new ViewFormErrorResDto(
                            f.getTitle(),
                            ViewFormErrorReason.RESPONSE_ALREADY_SUBMITTED,
                            "You have already submitted a response to this form."
                    )
            );
        }

        return getFormDetails(id);
    }

    @Override
    public List<FormSummaryResDto> getFormsSummaries(UUID userId) {
        return formRepo.findByUserIdOrderByLastOpenedOnDesc(userId)
                .stream()
                .map(f ->
                        new FormSummaryResDto(f.getId(), f.getName(), f.getLastOpenedOn())
                ).toList();
    }

    @Override
    public Form getFormById(UUID id) {
        return formRepo.findById(id).orElseThrow(() -> new FormNotFoundException(id));
    }

    @Override
    public SuccessMessageDto validateResponse(UUID formId, ResponseValidationRequestDto dto) {

        List<Long> requiredQuestionIds = new ArrayList<>();

        // Getting IDs of questions which are marked as required
        questionRepositoryFactory.getAll().forEach(qRepo -> {
            requiredQuestionIds.addAll(
                    qRepo.findByFormIdAndRequired(formId, true)
                            .stream().map(QuestionIdProjection::getId)
                            .toList()
            );
        });

        Set<Long> responseQuestionIds = new HashSet<>();

        // Putting question IDs of all responses into a HashSet
        dto.responses().forEach(vReq -> responseQuestionIds.add(vReq.getQuestionId()));

        List<String> missingQuestionIds = new ArrayList<>();

        // Checking if any ID is missing in the response question ID HashSet
        // If true put it in the missing list
        requiredQuestionIds.forEach(id -> {
            if (!responseQuestionIds.contains(id)) {
                missingQuestionIds.add(id.toString());
            }
        });

        if (!missingQuestionIds.isEmpty()) {
            throw new RequiredQuestionResponseNotFoundException(missingQuestionIds);
        }

        // Now passing each response into validators
        dto.responses().forEach(vReq -> {
            var questionManager = questionManagerFactory.get(vReq.getQuestionType());
            boolean isValid = questionManager.validateResponse(vReq);
            if (!isValid) {
                throw new ResponseValidationException(
                        String.format(ValidationMessages.INVALID_RESPONSE, vReq.getQuestionType())
                );
            }
        });

        return SuccessMessageDto.create(CommonMessages.ALL_RESPONSE_VALID);
    }

    @Override
    public SuccessMessageDto renameForm(UUID formId, FormRenameReqDto dto) {

        formRepo.renameForm(formId, dto.newName());

        return SuccessMessageDto.create("Form renamed successfully");
    }

    @Override
    public FormInfoResDto getFormInfo(UUID formId) {
        Form f = getFormById(formId);

        return FormInfoResDto.create(f);
    }

}
