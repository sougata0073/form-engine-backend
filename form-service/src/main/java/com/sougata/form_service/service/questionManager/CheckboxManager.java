package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationId;
import com.sougata.form_service.dto.question.request.CheckboxAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.CheckboxResDto;
import com.sougata.form_service.dto.validation.request.CheckboxValidationRequestDto;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.Checkbox;
import com.sougata.form_service.model.questionSchema.CheckboxOption;
import com.sougata.form_service.repository.CheckboxRepository;
import com.sougata.form_service.responseValidator.ResponseValidatorFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("CHECKBOX_QUESTION_MANAGER")
public class CheckboxManager extends QuestionManager<CheckboxAddUpdateReqDto, CheckboxResDto, CheckboxValidationRequestDto> {

    private final CheckboxRepository checkboxRepository;
    private final FormService formService;
    private final ResponseValidatorFactory responseValidatorFactory;

    @Autowired
    public CheckboxManager(CheckboxRepository checkboxRepository, FormService formService, ResponseValidatorFactory responseValidatorFactory) {
        this.checkboxRepository = checkboxRepository;
        this.formService = formService;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public CheckboxResDto get(UUID formId, Long questionId) {
        return CheckboxResDto.create(
                checkboxRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionNotFoundException(questionId))
        );
    }

    @Override
    public CheckboxResDto create(UUID formId, CheckboxAddUpdateReqDto crudDto) {
        Checkbox newCb = new Checkbox();

        setProperties(crudDto, formId, newCb);

        Checkbox savedCb = checkboxRepository.save(newCb);

        return CheckboxResDto.create(savedCb);
    }

    @Override
    public CheckboxResDto create(UUID formId, Long questionId, CheckboxAddUpdateReqDto crudDto) {
        Checkbox newCb = new Checkbox();

        newCb.setId(questionId);
        setProperties(crudDto, formId, newCb);

        Checkbox savedCb = checkboxRepository.save(newCb);

        return CheckboxResDto.create(savedCb);
    }

    @Override
    public CheckboxResDto update(Long questionId, CheckboxAddUpdateReqDto crudDto) {
        Checkbox cb = checkboxRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.CHECKBOX, questionId));
        setProperties(crudDto, cb);

        checkboxRepository.save(cb);

        return CheckboxResDto.create(cb);
    }

    @Override
    public void delete(Long questionId) {
        checkboxRepository.deleteById(questionId);
    }

    @Override
    public boolean exists(Long questionId) {
        return checkboxRepository.existsById(questionId);
    }

    @Override
    public boolean validateResponse(CheckboxValidationRequestDto validationDto) {
        Checkbox cb = checkboxRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.CHECKBOX, validationDto.getQuestionId()));

        var optionIdSet = new HashSet<>(cb.getOptions().stream().map(CheckboxOption::getId).toList());
        var invalidResponseOptionIds = new ArrayList<Long>();

        validationDto.getResponseOptionIds().forEach(id -> {
            if (!optionIdSet.contains(id)) {
                invalidResponseOptionIds.add(id);
            }
        });

        if (!invalidResponseOptionIds.isEmpty()) {
            throw new ResponseValidationException(
                    "The following option IDs are not valid for this question: " + invalidResponseOptionIds
            );
        }

        try {
            var validationId = ValidationId.valueOf(
                    JsonUtil.getValueFromOldJsonNode(cb.getValidationConfig(), "validationId")
            );
            var validator = responseValidatorFactory.getValidator(validationId);
            var validationConfig = JsonUtil.oldJsonNodeToObject(cb.getValidationConfig(), validator.getValidationConfigClass());
            return validator.isValid(validationDto, validationConfig);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(cb.getValidationConfig()));
        }
    }

    @Override
    public Class<CheckboxAddUpdateReqDto> getCrudDtoClass() {
        return CheckboxAddUpdateReqDto.class;
    }

    @Override
    public Class<CheckboxValidationRequestDto> getValidationDtoClass() {
        return CheckboxValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CheckboxRepository getQuestionRepository() {
        return checkboxRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

    private void setProperties(CheckboxAddUpdateReqDto source, UUID formId, Checkbox target) {

        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
        target.setOrderIndex(source.getOrderIndex());

        Map<Long, CheckboxOption> existingOptions = target.getOptions().stream()
                .collect(Collectors.toMap(CheckboxOption::getId, option -> option));

        Set<Long> requestOptionIds = source.getOptions().stream()
                .map(CheckboxAddUpdateReqDto.Option::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        target.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < source.getOptions().size(); i++) {

            var dto = source.getOptions().get(i);

            if (dto.id() == null) {

                CheckboxOption option = new CheckboxOption();
                option.setOption(dto.option());
                option.setOrderIndex(i);
                option.setCheckbox(target);

                target.getOptions().add(option);

            } else {

                CheckboxOption option = existingOptions.get(dto.id());

                if (option == null) {
                    throw new IllegalArgumentException("Invalid option id: " + dto.id());
                }

                option.setOption(dto.option());
                option.setOrderIndex(i);
            }
        }

        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(CheckboxAddUpdateReqDto source, Checkbox target) {
        setProperties(source, null, target);
    }

}
