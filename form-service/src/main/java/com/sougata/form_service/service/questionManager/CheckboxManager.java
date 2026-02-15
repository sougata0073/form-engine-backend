package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.CheckboxAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.CheckboxResDto;
import com.sougata.form_service.dto.validation.request.CheckboxValidationRequestDto;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.Checkbox;
import com.sougata.form_service.repository.CheckboxRepository;
import com.sougata.form_service.responseValidator.ResponseValidatorFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        try {
            var validator = responseValidatorFactory.getValidator(validationDto.getValidationId());
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
        return null;
    }

    private void setProperties(CheckboxAddUpdateReqDto source, UUID formId, Checkbox target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOptions(source.getOptions().toArray(new String[0]));
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(CheckboxAddUpdateReqDto source, Checkbox target) {
        setProperties(source, null, target);
    }

}
