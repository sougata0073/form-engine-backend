package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.CheckboxAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.CheckboxResDto;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Checkbox;
import com.sougata.form_service.model.questionSchema.CheckboxOption;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.CheckboxOptionRepository;
import com.sougata.form_service.repository.CheckboxRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import com.sougata.form_service.validation.configuration.ValidationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("CHECKBOX_QUESTION_MANAGER")
public class CheckboxManager extends QuestionManager<Checkbox, CheckboxAddUpdateReqDto, CheckboxResDto> {

    private final CheckboxRepository checkboxRepository;
    private final CheckboxOptionRepository checkboxOptionRepository;

    @Autowired
    public CheckboxManager(CheckboxRepository checkboxRepository, FormService formService, QuestionRepository questionRepository, CheckboxOptionRepository checkboxOptionRepository) {
        super(questionRepository, formService);
        this.checkboxRepository = checkboxRepository;
        this.checkboxOptionRepository = checkboxOptionRepository;
    }

    @Override
    public CheckboxResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                checkboxRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                        .orElseThrow(() -> new QuestionNotFoundException(questionId))
        );
    }

    @Override
    @Transactional
    public CheckboxResDto create(UUID formId, CheckboxAddUpdateReqDto crudDto) {
        var newCb = new Checkbox();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newCb, question);

        var savedCb = checkboxRepository.save(newCb);

        return toQuestionResDto(savedCb);
    }

    @Override
    public CheckboxResDto create(UUID formId, Long questionId, CheckboxAddUpdateReqDto crudDto) {
        var newCb = new Checkbox();

        var question = updateQuestion(formId, questionId, crudDto);

        setPropertiesForNew(crudDto, newCb, question);

        var savedCb = checkboxRepository.save(newCb);

        return toQuestionResDto(savedCb);
    }

    @Override
    @Transactional
    public CheckboxResDto update(UUID formId, Long questionId, CheckboxAddUpdateReqDto crudDto) {
        Checkbox cb = checkboxRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.CHECKBOX, questionId));

        updateQuestion(formId, questionId, crudDto);
        cb.setValidationConfig(JsonUtil.objectToOldJsonNode(crudDto.getValidationConfig()));

        Map<Long, CheckboxOption> existingOptions = cb.getOptions().stream()
                .collect(Collectors.toMap(CheckboxOption::getId, option -> option));
        Set<Long> requestOptionIds = crudDto.getOptions().stream()
                .map(CheckboxAddUpdateReqDto.Option::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        cb.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < crudDto.getOptions().size(); i++) {
            var dto = crudDto.getOptions().get(i);

            if (dto.getId() == null) {
                CheckboxOption option = new CheckboxOption();
                option.setOption(dto.getOption());
                option.setOrderIndex(i);
                option.setCheckbox(cb);

                cb.getOptions().add(option);
            } else {
                CheckboxOption option = existingOptions.get(dto.getId());
                if (option == null) {
                    throw new IllegalArgumentException("Invalid option id: " + dto.getId());
                }
                option.setOption(dto.getOption());
                option.setOrderIndex(i);
            }
        }

        checkboxRepository.save(cb);

        return toQuestionResDto(cb);
    }

    @Override
    public CheckboxResDto toQuestionResDto(Checkbox question) {
        var cb = new CheckboxResDto();

        populateCommonFields(question, cb);

        var options = question.getOptions().stream()
                .map(o ->
                        new CheckboxResDto.CheckboxOptionResDto(o.getId(), o.getOption(), o.getOrderIndex())
                )
                .sorted(Comparator.comparingInt(CheckboxResDto.CheckboxOptionResDto::getOrderIndex))
                .toList();

        cb.setOptions(options);

        try {
            cb.setValidationConfig(
                    JsonUtil.oldJsonNodeToObject(question.getValidationConfig(), ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(question.getValidationConfig()));
        }

        return cb;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

    @Override
    @Transactional
    public void delete(UUID formId, Long questionId) {
        checkboxOptionRepository.deleteAllByFormIdAndCheckboxId(formId, questionId);
        checkboxRepository.deleteQuestion(formId, questionId);
    }

    private void setPropertiesForNew(CheckboxAddUpdateReqDto source, Checkbox target, Question question) {
        var options = new ArrayList<CheckboxOption>();

        for (int i = 0; i < source.getOptions().size(); i++) {
            var op = source.getOptions().get(i);
            var cbOp = new CheckboxOption();

            cbOp.setOption(op.getOption());
            cbOp.setCheckbox(target);
            cbOp.setOrderIndex(i);

            options.add(cbOp);
        }

        target.setQuestion(question);
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
        target.setOptions(options);
    }

}
