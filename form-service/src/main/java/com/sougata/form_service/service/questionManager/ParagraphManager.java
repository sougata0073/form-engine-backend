package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationId;
import com.sougata.form_service.dto.question.request.ParagraphAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.ParagraphResDto;
import com.sougata.form_service.dto.validation.request.ParagraphValidationRequestDto;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Paragraph;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.ParagraphRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.responseValidator.ResponseValidatorFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("PARAGRAPH_QUESTION_MANAGER")
public class ParagraphManager extends QuestionManager<Paragraph, ParagraphAddUpdateReqDto, ParagraphResDto, ParagraphValidationRequestDto> {

    private final ParagraphRepository paragraphRepository;
    private final ResponseValidatorFactory responseValidatorFactory;

    public ParagraphManager(ParagraphRepository paragraphRepository, FormService formService, ResponseValidatorFactory responseValidatorFactory, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.paragraphRepository = paragraphRepository;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public ParagraphResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(paragraphRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public ParagraphResDto create(UUID formId, ParagraphAddUpdateReqDto crudDto) {
        var newP = new Paragraph();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newP, question);

        var saved = paragraphRepository.save(newP);

        return toQuestionResDto(saved);
    }

    @Override
    public ParagraphResDto create(UUID formId, Long questionId, ParagraphAddUpdateReqDto crudDto) {
        var newP = new Paragraph();

        var question = updateQuestion(questionId, crudDto);

        setPropertiesForNew(crudDto, newP, question);

        var saved = paragraphRepository.save(newP);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public ParagraphResDto update(Long questionId, ParagraphAddUpdateReqDto crudDto) {
        Paragraph p = paragraphRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.PARAGRAPH, questionId));

        updateQuestion(questionId, crudDto);
        p.setValidationConfig(JsonUtil.objectToOldJsonNode(crudDto.getValidationConfig()));

        paragraphRepository.save(p);

        return toQuestionResDto(p);
    }

    @Override
    public ParagraphResDto toQuestionResDto(Paragraph question) {
        var p = new ParagraphResDto();

        populateCommonFields(question, p);

        try {
            p.setValidationConfig(JsonUtil.oldJsonNodeToObject(question.getValidationConfig(), ValidationConfig.class));
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(question.getValidationConfig()));
        }

        return p;
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
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

    @Override
    public void delete(Long questionId) {
        paragraphRepository.deleteById(questionId);
    }

    private void setPropertiesForNew(ParagraphAddUpdateReqDto source, Paragraph target, Question question) {
        target.setQuestion(question);
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
    }
}
