package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.ShortAnswerAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_service.dto.validation.request.ShortAnswerValidationRequestDto;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.ShortAnswer;
import com.sougata.form_service.repository.ShortAnswerRepository;
import com.sougata.form_service.responseValidator.ResponseValidatorFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("SHORT_ANSWER_QUESTION_MANAGER")
public class ShortAnswerManager extends QuestionManager<ShortAnswerAddUpdateReqDto, ShortAnswerResDto, ShortAnswerValidationRequestDto> {

    private final ShortAnswerRepository shortAnswerRepository;
    private final FormService formService;
    private final ResponseValidatorFactory responseValidatorFactory;

    public ShortAnswerManager(ShortAnswerRepository shortAnswerRepository, FormService formService, ResponseValidatorFactory responseValidatorFactory) {
        this.shortAnswerRepository = shortAnswerRepository;
        this.formService = formService;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public ShortAnswerResDto create(UUID formId, ShortAnswerAddUpdateReqDto crudDto) {
        ShortAnswer newSa = new ShortAnswer();

        setProperties(crudDto, formId, newSa);

        ShortAnswer saved = shortAnswerRepository.save(newSa);

        return ShortAnswerResDto.create(saved);
    }

    @Override
    public ShortAnswerResDto create(UUID formId, Long questionId, ShortAnswerAddUpdateReqDto crudDto) {
        ShortAnswer newSa = new ShortAnswer();

        newSa.setId(questionId);
        setProperties(crudDto, formId, newSa);

        ShortAnswer saved = shortAnswerRepository.save(newSa);

        return ShortAnswerResDto.create(saved);
    }

    @Override
    public ShortAnswerResDto update(Long questionId, ShortAnswerAddUpdateReqDto crudDto) {
        ShortAnswer sa = shortAnswerRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.SHORT_ANSWER, questionId));
        setProperties(crudDto, sa);
        shortAnswerRepository.save(sa);

        return ShortAnswerResDto.create(sa);
    }

    @Override
    public boolean exists(Long questionId) {
        return shortAnswerRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        shortAnswerRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(ShortAnswerValidationRequestDto validationDto) {
        ShortAnswer sa = shortAnswerRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.SHORT_ANSWER, validationDto.getQuestionId()));

        try {
            var validator = responseValidatorFactory.getValidator(validationDto.getValidationId());
            var validationConfig = JsonUtil.oldJsonNodeToObject(sa.getValidationConfig(), validator.getValidationConfigClass());
            return validator.isValid(validationDto, validationConfig);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(sa.getValidationConfig()));
        }
    }

    @Override
    public Class<ShortAnswerAddUpdateReqDto> getCrudDtoClass() {
        return ShortAnswerAddUpdateReqDto.class;
    }

    @Override
    public Class<ShortAnswerValidationRequestDto> getValidationDtoClass() {
        return ShortAnswerValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ShortAnswerRepository getQuestionRepository() {
        return shortAnswerRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    private void setProperties(ShortAnswerAddUpdateReqDto source, UUID formId, ShortAnswer target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(ShortAnswerAddUpdateReqDto source, ShortAnswer target) {
        setProperties(source, null, target);
    }
}
