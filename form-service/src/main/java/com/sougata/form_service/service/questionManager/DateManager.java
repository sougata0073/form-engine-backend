package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DateAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DateResDto;
import com.sougata.form_service.dto.template.questionTemplate.DateTemplateDetails;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.Date;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.Question;
import com.sougata.form_service.repository.DateRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("DATE_QUESTION_MANAGER")
public class DateManager extends QuestionManager<Date, DateAddUpdateReqDto, DateResDto, DateTemplateDetails> {

    private final DateRepository dateRepository;

    public DateManager(DateRepository dateRepository, FormService formService, QuestionRepository questionRepository, QuestionRepository questionRepository1) {
        super(questionRepository, formService);
        this.dateRepository = dateRepository;
    }

    @Override
    public DateResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(dateRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public DateResDto create(UUID formId, DateAddUpdateReqDto crudDto) {
        var newDate = new Date();

        var question = createQuestion(crudDto, formId);

        newDate.setQuestion(question);

        var saved = dateRepository.save(newDate);

        return toQuestionResDto(saved, question);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public DateResDto create(UUID formId, Long questionId, DateAddUpdateReqDto questionAddUpdateReq) {
        var newDate = new Date();

        var question = updateQuestion(questionId, questionAddUpdateReq);

        newDate.setQuestion(question);

        var saved = dateRepository.save(newDate);

        return toQuestionResDto(saved, question);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public DateResDto update(UUID formId, Long questionId, DateAddUpdateReqDto questionAddUpdateReq) {
        Date date = dateRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DATE, questionId));

        var question = updateQuestion(questionId, questionAddUpdateReq);

        dateRepository.save(date);

        return toQuestionResDto(date, question);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        dateRepository.deleteQuestion(questionId);
    }

    @Override
    public DateResDto toQuestionResDto(Date childQuestion) {
        return toQuestionResDto(childQuestion, childQuestion.getQuestion());
    }

    @Override
    public DateResDto toQuestionResDto(Date childQuestion, Question parentQuestion) {
        var d = new DateResDto();

        populateCommonFields(parentQuestion, d);

        return d;
    }

    @Override
    public DateAddUpdateReqDto toQuestionAddUpdateReq(DateResDto questionRes) {
        var d = new DateAddUpdateReqDto();

        populateCommonFields(questionRes, d);

        return d;
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public Date createFromTemplate(DateTemplateDetails template, Form form) {
        var d = new Date();

        d.setQuestion(createQuestionFromTemplate(template, form));

        return dateRepository.save(d);
    }

}
