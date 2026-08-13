package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DateAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DateResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.Date;
import com.sougata.form_service.repository.DateRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("DATE_QUESTION_MANAGER")
public class DateManager extends QuestionManager<Date, DateAddUpdateReqDto, DateResDto> {

    private final DateRepository dateRepository;

    public DateManager(DateRepository dateRepository, FormService formService, QuestionRepository questionRepository, QuestionRepository questionRepository1) {
        super(questionRepository, formService);
        this.dateRepository = dateRepository;
    }

    @Override
    public DateResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(dateRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public DateResDto create(UUID formId, DateAddUpdateReqDto crudDto) {
        var newDate = new Date();

        var question = createQuestion(crudDto, formId);

        newDate.setQuestion(question);

        var saved = dateRepository.save(newDate);

        return toQuestionResDto(saved);
    }

    @Override
    public DateResDto create(UUID formId, Long questionId, DateAddUpdateReqDto crudDto) {
        var newDate = new Date();

        var question = updateQuestion(formId, questionId, crudDto);

        newDate.setQuestion(question);

        var saved = dateRepository.save(newDate);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public DateResDto update(UUID formId, Long questionId, DateAddUpdateReqDto crudDto) {
        Date date = dateRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DATE, questionId));

        updateQuestion(formId, questionId, crudDto);

        dateRepository.save(date);

        return toQuestionResDto(date);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        dateRepository.deleteQuestion(formId, questionId);
    }

    @Override
    public DateResDto toQuestionResDto(Date question) {
        var d = new DateResDto();

        populateCommonFields(question, d);

        return d;
    }

}
