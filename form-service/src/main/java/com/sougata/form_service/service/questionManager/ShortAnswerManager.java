package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.ShortAnswerAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.Question;
import com.sougata.form_service.model.ShortAnswer;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.repository.ShortAnswerRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import com.sougata.form_service.validation.configuration.ValidationConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("SHORT_ANSWER_QUESTION_MANAGER")
public class ShortAnswerManager extends QuestionManager<ShortAnswer, ShortAnswerAddUpdateReqDto, ShortAnswerResDto> {

    private final ShortAnswerRepository shortAnswerRepository;

    public ShortAnswerManager(ShortAnswerRepository shortAnswerRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.shortAnswerRepository = shortAnswerRepository;
    }

    @Override
    public ShortAnswerResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(shortAnswerRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
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

        var question = updateQuestion(formId, questionId, crudDto);

        setPropertiesForNew(crudDto, newS, question);

        var saved = shortAnswerRepository.save(newS);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public ShortAnswerResDto update(UUID formId, Long questionId, ShortAnswerAddUpdateReqDto crudDto) {
        ShortAnswer sa = shortAnswerRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.SHORT_ANSWER, questionId));

        updateQuestion(formId, questionId, crudDto);
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
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        shortAnswerRepository.deleteQuestion(formId, questionId);
    }

    private void setPropertiesForNew(ShortAnswerAddUpdateReqDto source, ShortAnswer target, Question question) {
        target.setQuestion(question);
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
    }
}
