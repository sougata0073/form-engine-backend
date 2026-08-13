package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.TimeAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.TimeResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.Time;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.repository.TimeRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("TIME_QUESTION_MANAGER")
public class TimeManager extends QuestionManager<Time, TimeAddUpdateReqDto, TimeResDto> {

    private final TimeRepository timeRepository;

    public TimeManager(TimeRepository timeRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.timeRepository = timeRepository;
    }

    @Override
    public TimeResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(timeRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public TimeResDto create(UUID formId, TimeAddUpdateReqDto crudDto) {
        var newTime = new Time();

        var question = createQuestion(crudDto, formId);

        newTime.setQuestion(question);

        var saved = timeRepository.save(newTime);

        return toQuestionResDto(saved);
    }

    @Override
    public TimeResDto create(UUID formId, Long questionId, TimeAddUpdateReqDto crudDto) {
        var newTime = new Time();

        var question = updateQuestion(formId, questionId, crudDto);

        newTime.setQuestion(question);

        var saved = timeRepository.save(newTime);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public TimeResDto update(UUID formId, Long questionId, TimeAddUpdateReqDto crudDto) {
        Time t = timeRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.TIME, questionId));

        updateQuestion(formId, questionId, crudDto);

        timeRepository.save(t);

        return toQuestionResDto(t);
    }

    @Override
    public TimeResDto toQuestionResDto(Time question) {
        var t = new TimeResDto();

        populateCommonFields(question, t);

        return t;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        timeRepository.deleteQuestion(formId, questionId);
    }
}
