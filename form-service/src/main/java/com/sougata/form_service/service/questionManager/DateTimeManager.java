package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DateTimeAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DateTimeResDto;
import com.sougata.form_service.dto.validation.request.DateTimeValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.DateTime;
import com.sougata.form_service.repository.DateTimeRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("DATE_TIME_QUESTION_MANAGER")
public class DateTimeManager extends QuestionManager<DateTimeAddUpdateReqDto, DateTimeResDto, DateTimeValidationRequestDto> {

    private final DateTimeRepository dateTimeRepository;
    private final FormService formService;

    public DateTimeManager(DateTimeRepository dateTimeRepository, FormService formService) {
        this.dateTimeRepository = dateTimeRepository;
        this.formService = formService;
    }

    @Override
    public DateTimeResDto create(UUID formId, DateTimeAddUpdateReqDto crudDto) {
        DateTime newDt = new DateTime();

        setProperties(crudDto, formId, newDt);

        DateTime saved = dateTimeRepository.save(newDt);

        return DateTimeResDto.create(saved);
    }

    @Override
    public DateTimeResDto create(UUID formId, Long questionId, DateTimeAddUpdateReqDto crudDto) {
        DateTime newDt = new DateTime();

        newDt.setId(questionId);
        setProperties(crudDto, formId, newDt);

        DateTime saved = dateTimeRepository.save(newDt);

        return DateTimeResDto.create(saved);
    }

    @Override
    public DateTimeResDto update(Long questionId, DateTimeAddUpdateReqDto crudDto) {
        DateTime dt = dateTimeRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DATE_TIME, questionId));
        setProperties(crudDto, dt);
        dateTimeRepository.save(dt);

        return DateTimeResDto.create(dt);
    }

    @Override
    public boolean exists(Long questionId) {
        return dateTimeRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        dateTimeRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(DateTimeValidationRequestDto validationDto) {
        return true;
    }

    @Override
    public Class<DateTimeAddUpdateReqDto> getCrudDtoClass() {
        return DateTimeAddUpdateReqDto.class;
    }

    @Override
    public Class<DateTimeValidationRequestDto> getValidationDtoClass() {
        return DateTimeValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DateTimeRepository getQuestionRepository() {
        return dateTimeRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    private void setProperties(DateTimeAddUpdateReqDto source, UUID formId, DateTime target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(DateTimeAddUpdateReqDto source, DateTime target) {
        setProperties(source, null, target);
    }
}
