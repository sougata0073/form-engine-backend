package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DateAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DateResDto;
import com.sougata.form_service.dto.validation.request.DateValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.Date;
import com.sougata.form_service.repository.DateRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("DATE_QUESTION_MANAGER")
public class DateManager extends QuestionManager<DateAddUpdateReqDto, DateResDto, DateValidationRequestDto> {

    private final DateRepository dateRepository;
    private final FormService formService;

    public DateManager(DateRepository dateRepository, FormService formService) {
        this.dateRepository = dateRepository;
        this.formService = formService;
    }

    @Override
    public DateResDto create(UUID formId, DateAddUpdateReqDto crudDto) {
        Date newDate = new Date();

        setProperties(crudDto, formId, newDate);

        Date saved = dateRepository.save(newDate);

        return DateResDto.create(saved);
    }

    @Override
    public DateResDto create(UUID formId, Long questionId, DateAddUpdateReqDto crudDto) {
        Date newDate = new Date();

        newDate.setId(questionId);
        setProperties(crudDto, formId, newDate);

        Date saved = dateRepository.save(newDate);

        return DateResDto.create(saved);
    }

    @Override
    public DateResDto update(Long questionId, DateAddUpdateReqDto crudDto) {
        Date date = dateRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DATE, questionId));
        setProperties(crudDto, date);
        dateRepository.save(date);

        return DateResDto.create(date);
    }

    @Override
    public boolean validateResponse(DateValidationRequestDto validationDto) {
        return true;
    }

    @Override
    public Class<DateAddUpdateReqDto> getCrudDtoClass() {
        return DateAddUpdateReqDto.class;
    }

    @Override
    public Class<DateValidationRequestDto> getValidationDtoClass() {
        return DateValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DateRepository getQuestionRepository() {
        return dateRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    private void setProperties(DateAddUpdateReqDto source, UUID formId, Date target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(DateAddUpdateReqDto source, Date target) {
        setProperties(source, null, target);
    }
}
