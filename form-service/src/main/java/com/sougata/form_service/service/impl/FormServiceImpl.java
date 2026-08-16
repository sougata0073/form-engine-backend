package com.sougata.form_service.service.impl;

import com.sougata.form_service.constant.ViewFormErrorReason;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.*;
import com.sougata.form_service.dto.template.TemplateDetails;
import com.sougata.form_service.exception.FormNotAcceptingResponseException;
import com.sougata.form_service.exception.FormNotFoundException;
import com.sougata.form_service.exception.FormResponseAlreadySubmittedException;
import com.sougata.form_service.feignClient.FormDataServiceFeignClient;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.repository.FormRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.FormServiceCached;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepo;
    private final FormDataServiceFeignClient formDataServiceFeignClient;
    private final FormServiceCached formServiceCached;
    private final QuestionManagerFactory questionManagerFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public FormServiceImpl(
            FormRepository formRepo,
            FormDataServiceFeignClient formDataServiceFeignClient,
            FormServiceCached formServiceCached,
            QuestionManagerFactory questionManagerFactory, RedisTemplate<String, Object> redisTemplate
    ) {
        this.formRepo = formRepo;
        this.formDataServiceFeignClient = formDataServiceFeignClient;
        this.formServiceCached = formServiceCached;
        this.questionManagerFactory = questionManagerFactory;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public FormInfoResDto createForm(FormAddUpdateReqDto dto, UUID userId) {
        var newForm = new Form();

        newForm.setUserId(userId);
        newForm.setName(dto.getName());
        newForm.setTitle(dto.getTitle());
        newForm.setDescription(dto.getDescription());
        newForm.setPublished(dto.getPublished());
        newForm.setAcceptingResponse(dto.getAcceptingResponse());
        newForm.setLastOpenedOn(Instant.now());

        var savedForm = formRepo.save(newForm);

        return FormInfoResDto.create(savedForm);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public FormInfoResDto copyForm(UUID formId, CopyFormReqDto req, UUID userId) {
        var referenceFormDetails = formServiceCached.getFormDetails(formId);
        var newForm = new Form();

        newForm.setUserId(userId);
        newForm.setName(req.getFormName());
        newForm.setTitle(referenceFormDetails.getTitle());
        newForm.setDescription(referenceFormDetails.getDescription());
        newForm.setPublished(referenceFormDetails.getPublished());
        newForm.setAcceptingResponse(referenceFormDetails.getAcceptingResponse());
        newForm.setLastOpenedOn(Instant.now());

        var savedForm = formRepo.save(newForm);

        referenceFormDetails.getQuestions().forEach(q -> {
            var manager = questionManagerFactory.get(q.getQuestionType());
            var addUpdateReq = manager.toQuestionAddUpdateReq(q);
            manager.create(savedForm.getId(), addUpdateReq);
        });

        return FormInfoResDto.create(savedForm);
    }

    @Override
    public FormInfoResDto updateForm(UUID formId, FormAddUpdateReqDto dto, UUID userId) {
        Form f = getFormById(formId);

        f.setUserId(userId);
        f.setName(dto.getName());
        f.setTitle(dto.getTitle());
        f.setDescription(dto.getDescription());
        f.setPublished(dto.getPublished());
        f.setAcceptingResponse(dto.getAcceptingResponse());
        f.setNotAcceptingResponseMessage(dto.getNotAcceptingResponseMessage());
        f.setStopAcceptingResponseOn(dto.getStopAcceptingResponseOn());
        f.setStopAcceptingResponseAfterResponse(dto.getStopAcceptingResponseAfterResponse());

        Form savedForm = formRepo.save(f);

        return FormInfoResDto.create(savedForm);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public FormResponseDto getForm(UUID id) {

        formRepo.updateLastOpenedOn(id, Instant.now());

        return formServiceCached.getFormDetails(id);
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
                formDataServiceFeignClient.getFormResponseSummary(f.getId()).getResponseCount() >= f.getStopAcceptingResponseAfterResponse();

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

        return formServiceCached.getFormDetails(id);
    }

    @Override
    public FormSummariesRes getFormsSummaries(UUID userId) {
        var forms = formRepo.findByUserIdOrderByLastOpenedOnDesc(userId)
                .stream()
                .map(f ->
                        new FormSummaryResDto(f.getId(), f.getName(), f.getLastOpenedOn())
                ).toList();

        return new FormSummariesRes(forms);
    }

    @Override
    public Form getFormById(UUID id) {
        return formRepo.findById(id).orElseThrow(() -> new FormNotFoundException(id));
    }

    @Override
    public SuccessMessageDto renameForm(UUID formId, FormRenameReqDto dto) {

        formRepo.renameForm(formId, dto.getNewName());

        return SuccessMessageDto.create("Form renamed successfully");
    }

    @Override
    public FormInfoResDto getFormInfo(UUID formId) {
        Form f = getFormById(formId);

        return FormInfoResDto.create(f);
    }

    @Override
    public SuccessMessageDto deleteForm(UUID formId) {
        formRepo.deleteById(formId);

        return new SuccessMessageDto("Form deleted successfully with ID: " + formId);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public Form createFromTemplate(TemplateDetails template, UUID userId) {
        Form f = new Form();

        f.setUserId(userId);
        f.setName(template.getName());
        f.setTitle(template.getTitle());
        f.setDescription(template.getDescription());
        f.setPublished(false);
        f.setAcceptingResponse(true);
        f.setLastOpenedOn(Instant.now());

        var savedForm = formRepo.save(f);

        template.getQuestionTemplates().forEach(qt -> {
            var questionManager = questionManagerFactory.get(qt.getQuestionType());
            questionManager.createFromTemplate(qt, savedForm);
        });

        return savedForm;
    }

}
