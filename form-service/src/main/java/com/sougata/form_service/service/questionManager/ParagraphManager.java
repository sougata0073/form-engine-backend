package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationId;
import com.sougata.form_service.dto.question.request.ParagraphAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.ParagraphResDto;
import com.sougata.form_service.dto.validation.request.ParagraphValidationRequestDto;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Paragraph;
import com.sougata.form_service.repository.ParagraphRepository;
import com.sougata.form_service.responseValidator.ResponseValidatorFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("PARAGRAPH_QUESTION_MANAGER")
public class ParagraphManager extends QuestionManager<ParagraphAddUpdateReqDto, ParagraphResDto, ParagraphValidationRequestDto> {

    private final ParagraphRepository paragraphRepository;
    private final FormService formService;
    private final ResponseValidatorFactory responseValidatorFactory;

    public ParagraphManager(ParagraphRepository paragraphRepository, FormService formService, ResponseValidatorFactory responseValidatorFactory) {
        this.paragraphRepository = paragraphRepository;
        this.formService = formService;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public ParagraphResDto get(UUID formId, Long questionId) {
        return ParagraphResDto.create(paragraphRepository.findByFormIdAndId(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    public ParagraphResDto create(UUID formId, ParagraphAddUpdateReqDto crudDto) {
        Paragraph newP = new Paragraph();

        setProperties(crudDto, formId, newP);

        Paragraph saved = paragraphRepository.save(newP);

        return ParagraphResDto.create(saved);
    }

    @Override
    public ParagraphResDto create(UUID formId, Long questionId, ParagraphAddUpdateReqDto crudDto) {
        Paragraph newP = new Paragraph();

        newP.setId(questionId);
        setProperties(crudDto, formId, newP);

        Paragraph saved = paragraphRepository.save(newP);

        return ParagraphResDto.create(saved);
    }

    @Override
    public ParagraphResDto update(Long questionId, ParagraphAddUpdateReqDto crudDto) {
        Paragraph p = paragraphRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.PARAGRAPH, questionId));
        setProperties(crudDto, p);
        paragraphRepository.save(p);

        return ParagraphResDto.create(p);
    }

    @Override
    public boolean exists(Long questionId) {
        return paragraphRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        paragraphRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(ParagraphValidationRequestDto validationDto) {
        var vConfig = paragraphRepository.getValidationConfig(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.PARAGRAPH, validationDto.getQuestionId()));

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
    public Class<ParagraphAddUpdateReqDto> getCrudDtoClass() {
        return ParagraphAddUpdateReqDto.class;
    }

    @Override
    public Class<ParagraphValidationRequestDto> getValidationDtoClass() {
        return ParagraphValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ParagraphRepository getQuestionRepository() {
        return paragraphRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

    private void setProperties(ParagraphAddUpdateReqDto source, UUID formId, Paragraph target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(ParagraphAddUpdateReqDto source, Paragraph target) {
        setProperties(source, null, target);
    }
}
