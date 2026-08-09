package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DropdownAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DropdownResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Dropdown;
import com.sougata.form_service.model.questionSchema.DropdownOption;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.DropdownOptionRepository;
import com.sougata.form_service.repository.DropdownRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("DROPDOWN_QUESTION_MANAGER")
public class DropdownManager extends QuestionManager<Dropdown, DropdownAddUpdateReqDto, DropdownResDto> {

    private final DropdownRepository dropdownRepository;
    private final DropdownOptionRepository dropdownOptionRepository;

    public DropdownManager(DropdownRepository dropdownRepository, FormService formService, QuestionRepository questionRepository, DropdownOptionRepository dropdownOptionRepository) {
        super(questionRepository, formService);
        this.dropdownRepository = dropdownRepository;
        this.dropdownOptionRepository = dropdownOptionRepository;
    }

    @Override
    public DropdownResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(dropdownRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public DropdownResDto create(UUID formId, DropdownAddUpdateReqDto crudDto) {
        var newDd = new Dropdown();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newDd, question);

        var saved = dropdownRepository.save(newDd);

        return toQuestionResDto(saved);
    }

    @Override
    public DropdownResDto create(UUID formId, Long questionId, DropdownAddUpdateReqDto crudDto) {
        var newDd = new Dropdown();

        var question = updateQuestion(formId, questionId, crudDto);

        setPropertiesForNew(crudDto, newDd, question);

        var saved = dropdownRepository.save(newDd);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public DropdownResDto update(UUID formId, Long questionId, DropdownAddUpdateReqDto crudDto) {
        Dropdown dd = dropdownRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DROPDOWN, questionId));

        updateQuestion(formId, questionId, crudDto);

        Map<Long, DropdownOption> existingOptions = dd.getOptions().stream()
                .collect(Collectors.toMap(DropdownOption::getId, option -> option));
        Set<Long> requestOptionIds = crudDto.getOptions().stream()
                .map(DropdownAddUpdateReqDto.Option::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        dd.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < crudDto.getOptions().size(); i++) {
            var dto = crudDto.getOptions().get(i);

            if (dto.getId() == null) {
                DropdownOption option = new DropdownOption();
                option.setOption(dto.getOption());
                option.setOrderIndex(i);
                option.setDropdown(dd);

                dd.getOptions().add(option);
            } else {
                DropdownOption option = existingOptions.get(dto.getId());
                if (option == null) {
                    throw new IllegalArgumentException("Invalid dropdown option id: " + dto.getId());
                }
                option.setOption(dto.getOption());
                option.setOrderIndex(i);
            }
        }

        dropdownRepository.save(dd);

        return toQuestionResDto(dd);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

    @Override
    @Transactional
    public void delete(UUID formId, Long questionId) {
        dropdownOptionRepository.deleteAllByFormIdAndDropdownId(formId, questionId);
        dropdownRepository.deleteQuestion(formId, questionId);
    }

    @Override
    public DropdownResDto toQuestionResDto(Dropdown question) {
        var dd = new DropdownResDto();

        populateCommonFields(question, dd);

        var options = question.getOptions().stream()
                .map(o -> new DropdownResDto.DropdownOptionResDto(o.getId(), o.getOption(), o.getOrderIndex()))
                .sorted(Comparator.comparingInt(DropdownResDto.DropdownOptionResDto::getOrderIndex))
                .toList();

        dd.setOptions(options);

        return dd;
    }

    private void setPropertiesForNew(DropdownAddUpdateReqDto source, Dropdown target, Question question) {
        var options = new ArrayList<DropdownOption>();

        for (int i = 0; i < source.getOptions().size(); i++) {
            var op = source.getOptions().get(i);
            var ddOp = new DropdownOption();

            ddOp.setOption(op.getOption());
            ddOp.setDropdown(target);
            ddOp.setOrderIndex(i);

            options.add(ddOp);
        }

        target.setQuestion(question);
        target.setOptions(options);
    }
}
