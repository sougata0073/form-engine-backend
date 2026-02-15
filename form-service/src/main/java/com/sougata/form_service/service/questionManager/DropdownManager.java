package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.ExceptionMessages;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DropdownAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DropdownResDto;
import com.sougata.form_service.dto.validation.request.DropdownValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.Dropdown;
import com.sougata.form_service.repository.DropdownRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("DROPDOWN_QUESTION_MANAGER")
public class DropdownManager extends QuestionManager<DropdownAddUpdateReqDto, DropdownResDto, DropdownValidationRequestDto> {

    private final DropdownRepository dropdownRepository;
    private final FormService formService;

    public DropdownManager(DropdownRepository dropdownRepository, FormService formService) {
        this.dropdownRepository = dropdownRepository;
        this.formService = formService;
    }

    @Override
    public DropdownResDto create(UUID formId, DropdownAddUpdateReqDto crudDto) {
        Dropdown newDd = new Dropdown();

        setProperties(crudDto, formId, newDd);

        Dropdown saved = dropdownRepository.save(newDd);

        return DropdownResDto.create(saved);
    }

    @Override
    public DropdownResDto create(UUID formId, Long questionId, DropdownAddUpdateReqDto crudDto) {
        Dropdown newDd = new Dropdown();

        newDd.setId(questionId);
        setProperties(crudDto, formId, newDd);

        Dropdown saved = dropdownRepository.save(newDd);

        return DropdownResDto.create(saved);
    }

    @Override
    public DropdownResDto update(Long questionId, DropdownAddUpdateReqDto crudDto) {
        Dropdown dd = dropdownRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DROPDOWN, questionId));
        setProperties(crudDto, dd);
        dropdownRepository.save(dd);

        return DropdownResDto.create(dd);
    }

    @Override
    public boolean exists(Long questionId) {
        return dropdownRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        dropdownRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(DropdownValidationRequestDto validationDto) {
        Dropdown dd = dropdownRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DROPDOWN, validationDto.getQuestionId()));

        if (validationDto.getResponseIndex() >= dd.getOptions().length) {
            throw new ResponseValidationException(String.format(ExceptionMessages.INVALID_DROPDOWN_SELECTED_INDEX, validationDto.getResponseIndex()));
        }

        return true;
    }

    @Override
    public Class<DropdownAddUpdateReqDto> getCrudDtoClass() {
        return DropdownAddUpdateReqDto.class;
    }

    @Override
    public Class<DropdownValidationRequestDto> getValidationDtoClass() {
        return DropdownValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DropdownRepository getQuestionRepository() {
        return dropdownRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

    private void setProperties(DropdownAddUpdateReqDto source, UUID formId, Dropdown target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOptions(source.getOptions().toArray(new String[0]));
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(DropdownAddUpdateReqDto source, Dropdown target) {
        setProperties(source, null, target);
    }
}
