package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationId;
import com.sougata.form_service.dto.question.request.ShortAnswerAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_service.dto.validation.request.ShortAnswerValidationRequestDto;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.model.questionSchema.ShortAnswer;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.repository.ShortAnswerRepository;
import com.sougata.form_service.responseValidator.ResponseValidatorFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("SHORT_ANSWER_QUESTION_MANAGER")
public class ShortAnswerManager extends QuestionManager<ShortAnswer, ShortAnswerAddUpdateReqDto, ShortAnswerResDto, ShortAnswerValidationRequestDto> {

    private final ShortAnswerRepository shortAnswerRepository;
    private final ResponseValidatorFactory responseValidatorFactory;

    public ShortAnswerManager(ShortAnswerRepository shortAnswerRepository, FormService formService, ResponseValidatorFactory responseValidatorFactory, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.shortAnswerRepository = shortAnswerRepository;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public ShortAnswerResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(shortAnswerRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public ShortAnswerResDto create(UUID formId, ShortAnswerAddUpdateReqDto crudDto) {
        var newS = new ShortAnswer();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newS, question);

        var saved = shortAnswerRepository.save(newS);

        return toQuestionResDto(saved);
    }

    @Override
    public ShortAnswerResDto create(UUID formId, Long questionId, ShortAnswerAddUpdateReqDto crudDto) {
        var newS = new ShortAnswer();

        var question = updateQuestion(questionId, crudDto);

        setPropertiesForNew(crudDto, newS, question);

        var saved = shortAnswerRepository.save(newS);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public ShortAnswerResDto update(Long questionId, ShortAnswerAddUpdateReqDto crudDto) {
        ShortAnswer sa = shortAnswerRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.SHORT_ANSWER, questionId));

        updateQuestion(questionId, crudDto);
        sa.setValidationConfig(JsonUtil.objectToOldJsonNode(crudDto.getValidationConfig()));

        shortAnswerRepository.save(sa);

        return toQuestionResDto(sa);
    }

    @Override
    public ShortAnswerResDto toQuestionResDto(ShortAnswer question) {
        var s = new ShortAnswerResDto();

        populateCommonFields(question, s);

        try {
            s.setValidationConfig(JsonUtil.oldJsonNodeToObject(question.getValidationConfig(), ValidationConfig.class));
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(question.getValidationConfig()));
        }

        return s;
    }

    @Override
    public boolean validateResponse(ShortAnswerValidationRequestDto validationDto) {
        var vConfig = shortAnswerRepository.getValidationConfig(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.SHORT_ANSWER, validationDto.getQuestionId()));

        try {
            var validationId = ValidationId.valueOf(
                    JsonUtil.getValueFromOldJsonNode(vConfig, "validationId")
            );

            var validator = responseValidatorFactory.getValidator(validationId);
            var validationConfig = JsonUtil.oldJsonNodeToObject(vConfig, validator.getValidationConfigClass());
            return validator.isValid(validationDto, validationConfig);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(vConfig));
        }
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    public void delete(Long questionId) {
        shortAnswerRepository.deleteById(questionId);
    }

    private void setPropertiesForNew(ShortAnswerAddUpdateReqDto source, ShortAnswer target, Question question) {
        target.setQuestion(question);
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
    }
}
