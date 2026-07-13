package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DropdownAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DropdownResDto;
import com.sougata.form_service.dto.validation.request.DropdownValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.Dropdown;
import com.sougata.form_service.model.questionSchema.DropdownOption;
import com.sougata.form_service.repository.DropdownRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("DROPDOWN_QUESTION_MANAGER")
public class DropdownManager extends QuestionManager<DropdownAddUpdateReqDto, DropdownResDto, DropdownValidationRequestDto> {

    private final DropdownRepository dropdownRepository;
    private final FormService formService;

    public DropdownManager(DropdownRepository dropdownRepository, FormService formService) {
        this.dropdownRepository = dropdownRepository;
        this.formService = formService;
    }

    @Override
    public DropdownResDto get(UUID formId, Long questionId) {
        return DropdownResDto.create(dropdownRepository.findByFormIdAndId(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
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

        var present = dd.getOptions()
                .stream().anyMatch(op -> Objects.equals(op.getId(), validationDto.getResponseOptionId()));

        if(!present) {
            throw new ResponseValidationException(
                    "Invalid dropdown option ID: " + validationDto.getResponseOptionId()
            );
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
        target.setOrderIndex(source.getOrderIndex());

        Map<Long, DropdownOption> existingOptions = target.getOptions().stream()
                .collect(Collectors.toMap(DropdownOption::getId, option -> option));

        Set<Long> requestOptionIds = source.getOptions().stream()
                .map(DropdownAddUpdateReqDto.Option::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        target.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < source.getOptions().size(); i++) {

            var dto = source.getOptions().get(i);

            if (dto.id() == null) {

                DropdownOption option = new DropdownOption();
                option.setOption(dto.option());
                option.setOrderIndex(i);
                option.setDropdown(target);

                target.getOptions().add(option);

            } else {

                DropdownOption option = existingOptions.get(dto.id());

                if (option == null) {
                    throw new IllegalArgumentException("Invalid dropdown option id: " + dto.id());
                }

                option.setOption(dto.option());
                option.setOrderIndex(i);
            }
        }

        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(DropdownAddUpdateReqDto source, Dropdown target) {
        setProperties(source, null, target);
    }
}
