package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DateTimeAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DateTimeResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.DateTime;
import com.sougata.form_service.repository.DateTimeRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("DATE_TIME_QUESTION_MANAGER")
public class DateTimeManager extends QuestionManager<DateTime, DateTimeAddUpdateReqDto, DateTimeResDto> {

    private final DateTimeRepository dateTimeRepository;

    public DateTimeManager(DateTimeRepository dateTimeRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.dateTimeRepository = dateTimeRepository;
    }

    @Override
    public DateTimeResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(dateTimeRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public DateTimeResDto create(UUID formId, DateTimeAddUpdateReqDto crudDto) {
        var newDt = new DateTime();

        var question = createQuestion(crudDto, formId);

        newDt.setQuestion(question);

        var saved = dateTimeRepository.save(newDt);

        return toQuestionResDto(saved);
    }

    @Override
    public DateTimeResDto create(UUID formId, Long questionId, DateTimeAddUpdateReqDto crudDto) {
        var newDt = new DateTime();

        var question = updateQuestion(questionId, crudDto);

        newDt.setQuestion(question);

        var saved = dateTimeRepository.save(newDt);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public DateTimeResDto update(Long questionId, DateTimeAddUpdateReqDto crudDto) {
        var dt = dateTimeRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DATE_TIME, questionId));

        updateQuestion(questionId, crudDto);

        dateTimeRepository.save(dt);

        return toQuestionResDto(dt);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        dateTimeRepository.deleteQuestion(formId, questionId);
    }

    @Override
    public DateTimeResDto toQuestionResDto(DateTime question) {
        var dt = new DateTimeResDto();

        populateCommonFields(question, dt);

        return dt;
    }
}
