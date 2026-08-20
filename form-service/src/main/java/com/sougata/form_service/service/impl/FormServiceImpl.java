package com.sougata.form_service.service.impl;

import com.sougata.form_service.configuration.AppConfiguration;
import com.sougata.form_service.constant.CommonCacheNames;
import com.sougata.form_service.constant.ViewFormErrorReason;
import com.sougata.form_service.constant.cacheNames.FormCacheNames;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepo;
    private final FormDataServiceFeignClient formDataServiceFeignClient;
    private final FormServiceCached formServiceCached;
    private final QuestionManagerFactory questionManagerFactory;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AppConfiguration appConfiguration;

    @Autowired
    public FormServiceImpl(
            FormRepository formRepo,
            FormDataServiceFeignClient formDataServiceFeignClient,
            FormServiceCached formServiceCached,
            QuestionManagerFactory questionManagerFactory, RedisTemplate<String, Object> redisTemplate, AppConfiguration appConfiguration
    ) {
        this.formRepo = formRepo;
        this.formDataServiceFeignClient = formDataServiceFeignClient;
        this.formServiceCached = formServiceCached;
        this.questionManagerFactory = questionManagerFactory;
        this.redisTemplate = redisTemplate;
        this.appConfiguration = appConfiguration;
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

        var formInfo = FormInfoResDto.create(savedForm);

        addFirstInRecentForms(userId, formInfo);

        return formInfo;
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

        var formInfo = FormInfoResDto.create(savedForm);

        addFirstInRecentForms(userId, formInfo);

        return formInfo;
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

        var formInfo = FormInfoResDto.create(savedForm);

        updateRecentForms(userId, formInfo);
        updateFormDetails(formInfo);
        updateFormInfo(formInfo);

        return formInfo;
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public FormResponseDto getForm(UUID formId, UUID userId) {

        var currTime = Instant.now();

        formRepo.updateLastOpenedOn(formId, currTime);

        var recentFormsCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.RECENT_FORMS + CommonCacheNames.SEPARATOR + userId;

        if (redisTemplate.hasKey(recentFormsCacheKey)) {
            var formSummaries = (FormSummariesRes) redisTemplate.opsForValue().get(recentFormsCacheKey);

            formSummaries.getForms().forEach(f -> {
                if (f.getId().equals(formId)) {
                    f.setLastOpenedOn(currTime);
                }
            });
            formSummaries.getForms().sort(Comparator.comparing(FormSummaryResDto::getLastOpenedOn));

            redisTemplate.opsForValue().set(recentFormsCacheKey, formSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }

        var formDetailsCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_DETAILS + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(formDetailsCacheKey)) {
            var formDetails = (FormResponseDto) redisTemplate.opsForValue().get(formDetailsCacheKey);

            formDetails.setLastOpenedOn(currTime);

            redisTemplate.opsForValue().set(formDetailsCacheKey, formDetails, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }

        var formInfoCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_INFO + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(formInfoCacheKey)) {
            var prevFormInfo = (FormInfoResDto) redisTemplate.opsForValue().get(formInfoCacheKey);

            prevFormInfo.setLastOpenedOn(currTime);

            redisTemplate.opsForValue().set(formInfoCacheKey, prevFormInfo, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }

        return formServiceCached.getFormDetails(formId);
    }

    @Override
    public FormResponseDto viewForm(UUID id, UUID userId) {
        var f = formServiceCached.getFormDetails(id);

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

        return f;
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
    public SuccessMessageDto renameForm(UUID formId, FormRenameReqDto dto, UUID userId) {

        formRepo.renameForm(formId, dto.getNewName());

        var recentFormsCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.RECENT_FORMS + CommonCacheNames.SEPARATOR + userId;

        if (redisTemplate.hasKey(recentFormsCacheKey)) {
            var formSummaries = (FormSummariesRes) redisTemplate.opsForValue().get(recentFormsCacheKey);

            formSummaries.getForms().forEach(f -> {
                if (f.getId().equals(formId)) {
                    f.setName(dto.getNewName());
                }
            });

            redisTemplate.opsForValue().set(recentFormsCacheKey, formSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }


        var formDetailsCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_DETAILS + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(formDetailsCacheKey)) {
            var formDetails = (FormResponseDto) redisTemplate.opsForValue().get(formDetailsCacheKey);

            formDetails.setName(dto.getNewName());

            redisTemplate.opsForValue().set(formDetailsCacheKey, formDetails, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }

        var formInfoCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_INFO + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(formInfoCacheKey)) {
            var prevFormInfo = (FormInfoResDto) redisTemplate.opsForValue().get(formInfoCacheKey);

            prevFormInfo.setName(dto.getNewName());

            redisTemplate.opsForValue().set(formInfoCacheKey, prevFormInfo, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }

        return SuccessMessageDto.create("Form renamed successfully");
    }

    @Override
    public FormInfoResDto getFormInfo(UUID formId) {
        Form f = getFormById(formId);

        return FormInfoResDto.create(f);
    }

    @Override
    public SuccessMessageDto deleteForm(UUID formId, UUID userId) {
        formRepo.deleteById(formId);

        deleteFormFromRecentForms(userId, formId);

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

        addFirstInRecentForms(userId, FormInfoResDto.create(savedForm));

        return savedForm;
    }

    private void addFirstInRecentForms(UUID userId, FormInfoResDto formInfo) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.RECENT_FORMS + CommonCacheNames.SEPARATOR + userId;

        if (redisTemplate.hasKey(cacheKey)) {
            var formSummaries = (FormSummariesRes) redisTemplate.opsForValue().get(cacheKey);

            formSummaries.getForms().addFirst(new FormSummaryResDto(formInfo.getId(), formInfo.getName(), formInfo.getLastOpenedOn()));

            redisTemplate.opsForValue().set(cacheKey, formSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void deleteFormFromRecentForms(UUID userId, UUID formId) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.RECENT_FORMS + CommonCacheNames.SEPARATOR + userId;

        if (redisTemplate.hasKey(cacheKey)) {
            var formSummaries = (FormSummariesRes) redisTemplate.opsForValue().get(cacheKey);

            formSummaries.getForms().removeIf(f -> f.getId().equals(formId));

            redisTemplate.opsForValue().set(cacheKey, formSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void updateRecentForms(UUID userId, FormInfoResDto formInfo) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.RECENT_FORMS + CommonCacheNames.SEPARATOR + userId;

        if (redisTemplate.hasKey(cacheKey)) {
            var formSummaries = (FormSummariesRes) redisTemplate.opsForValue().get(cacheKey);

            formSummaries.getForms().forEach(f -> {
                if (f.getId().equals(formInfo.getId())) {
                    f.setName(formInfo.getName());
                }
            });

            redisTemplate.opsForValue().set(cacheKey, formSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void updateFormDetails(FormInfoResDto formInfo) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_DETAILS + CommonCacheNames.SEPARATOR + formInfo.getId();

        if (redisTemplate.hasKey(cacheKey)) {
            var formDetails = (FormResponseDto) redisTemplate.opsForValue().get(cacheKey);

            formDetails.setName(formInfo.getName());
            formDetails.setTitle(formInfo.getTitle());
            formDetails.setDescription(formInfo.getDescription());
            formDetails.setPublished(formInfo.getPublished());
            formDetails.setAcceptingResponse(formInfo.getAcceptingResponse());
            formDetails.setNotAcceptingResponseMessage(formInfo.getNotAcceptingResponseMessage());
            formDetails.setStopAcceptingResponseOn(formInfo.getStopAcceptingResponseOn());
            formDetails.setStopAcceptingResponseAfterResponse(formInfo.getStopAcceptingResponseAfterResponse());
            formDetails.setLastOpenedOn(formInfo.getLastOpenedOn());

            redisTemplate.opsForValue().set(cacheKey, formDetails, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void updateFormInfo(FormInfoResDto formInfo) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_INFO + CommonCacheNames.SEPARATOR + formInfo.getId();

        redisTemplate.opsForValue().set(cacheKey, formInfo, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
    }

}
