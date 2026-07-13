package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.TimeAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.TimeResDto;
import com.sougata.form_service.dto.validation.request.TimeValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Time;
import com.sougata.form_service.repository.TimeRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("TIME_QUESTION_MANAGER")
public class TimeManager extends QuestionManager<TimeAddUpdateReqDto, TimeResDto, TimeValidationRequestDto> {

    private final TimeRepository timeRepository;
    private final FormService formService;

    public TimeManager(TimeRepository timeRepository, FormService formService) {
        this.timeRepository = timeRepository;
        this.formService = formService;
    }

    @Override
    public TimeResDto get(UUID formId, Long questionId) {
        return TimeResDto.create(timeRepository.findByFormIdAndId(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    public TimeResDto create(UUID formId, TimeAddUpdateReqDto crudDto) {
        Time newT = new Time();

        setProperties(crudDto, formId, newT);

        Time saved = timeRepository.save(newT);

        return TimeResDto.create(saved);
    }

    @Override
    public TimeResDto create(UUID formId, Long questionId, TimeAddUpdateReqDto crudDto) {
        Time newT = new Time();

        newT.setId(questionId);
        setProperties(crudDto, formId, newT);

        Time saved = timeRepository.save(newT);

        return TimeResDto.create(saved);
    }

    @Override
    public TimeResDto update(Long questionId, TimeAddUpdateReqDto crudDto) {
        Time t = timeRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.TIME, questionId));
        setProperties(crudDto, t);
        timeRepository.save(t);

        return TimeResDto.create(t);
    }

    @Override
    public boolean exists(Long questionId) {
        return timeRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        timeRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(TimeValidationRequestDto validationDto) {
        return true;
    }

    @Override
    public Class<TimeAddUpdateReqDto> getCrudDtoClass() {
        return TimeAddUpdateReqDto.class;
    }

    @Override
    public Class<TimeValidationRequestDto> getValidationDtoClass() {
        return TimeValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TimeRepository getQuestionRepository() {
        return timeRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

    private void setProperties(TimeAddUpdateReqDto source, UUID formId, Time target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(TimeAddUpdateReqDto source, Time target) {
        setProperties(source, null, target);
    }
}
