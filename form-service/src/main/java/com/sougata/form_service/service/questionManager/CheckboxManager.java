package com.sougata.form_service.service.questionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationId;
import com.sougata.form_service.dto.question.request.CheckboxAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.CheckboxResDto;
import com.sougata.form_service.dto.validation.request.CheckboxValidationRequestDto;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.Checkbox;
import com.sougata.form_service.model.questionSchema.CheckboxOption;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.CheckboxRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.responseValidator.ResponseValidatorFactory;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import com.sougata.form_service.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("CHECKBOX_QUESTION_MANAGER")
public class CheckboxManager extends QuestionManager<Checkbox, CheckboxAddUpdateReqDto, CheckboxResDto, CheckboxValidationRequestDto> {

    private final CheckboxRepository checkboxRepository;
    private final ResponseValidatorFactory responseValidatorFactory;

    @Autowired
    public CheckboxManager(CheckboxRepository checkboxRepository, FormService formService, ResponseValidatorFactory responseValidatorFactory, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.checkboxRepository = checkboxRepository;
        this.responseValidatorFactory = responseValidatorFactory;
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

        var question = updateQuestion(questionId, crudDto);

        setPropertiesForNew(crudDto, newCb, question);

        var savedCb = checkboxRepository.save(newCb);

        return toQuestionResDto(savedCb);
    }

    @Override
    @Transactional
    public CheckboxResDto update(Long questionId, CheckboxAddUpdateReqDto crudDto) {
        Checkbox cb = checkboxRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.CHECKBOX, questionId));

        updateQuestion(questionId, crudDto);
        cb.setValidationConfig(JsonUtil.objectToOldJsonNode(crudDto.getValidationConfig()));

        Map<Long, CheckboxOption> existingOptions = cb.getOptions().stream()
                .collect(Collectors.toMap(CheckboxOption::getId, option -> option));
        Set<Long> requestOptionIds = crudDto.getOptions().stream()
                .map(CheckboxAddUpdateReqDto.Option::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        cb.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < crudDto.getOptions().size(); i++) {
            var dto = crudDto.getOptions().get(i);

            if (dto.id() == null) {
                CheckboxOption option = new CheckboxOption();
                option.setOption(dto.option());
                option.setOrderIndex(i);
                option.setCheckbox(cb);

                cb.getOptions().add(option);
            } else {
                CheckboxOption option = existingOptions.get(dto.id());
                if (option == null) {
                    throw new IllegalArgumentException("Invalid option id: " + dto.id());
                }
                option.setOption(dto.option());
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

        cb.setOptions(
                question.getOptions().stream()
                        .map(o ->
                                new CheckboxResDto.CheckboxOptionResDto(o.getId(), o.getOption(), o.getOrderIndex())
                        )
                        .toList()
        );

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
    public boolean validateResponse(CheckboxValidationRequestDto validationDto) {
        Checkbox cb = checkboxRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.CHECKBOX, validationDto.getQuestionId()));

        var optionIdSet = new HashSet<>(cb.getOptions().stream().map(CheckboxOption::getId).toList());
        var invalidResponseOptionIds = new ArrayList<Long>();

        validationDto.getResponseOptionIds().forEach(id -> {
            if (!optionIdSet.contains(id)) {
                invalidResponseOptionIds.add(id);
            }
        });

        if (!invalidResponseOptionIds.isEmpty()) {
            throw new ResponseValidationException(
                    "The following option IDs are not valid for this question: " + invalidResponseOptionIds
            );
        }

        try {
            var validationId = ValidationId.valueOf(
                    JsonUtil.getValueFromOldJsonNode(cb.getValidationConfig(), "validationId")
            );
            var validator = responseValidatorFactory.getValidator(validationId);
            var validationConfig = JsonUtil.oldJsonNodeToObject(cb.getValidationConfig(), validator.getValidationConfigClass());
            return validator.isValid(validationDto, validationConfig);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(cb.getValidationConfig()));
        }
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

    @Override
    public void delete(Long questionId) {
        checkboxRepository.deleteById(questionId);
    }

    private void setPropertiesForNew(CheckboxAddUpdateReqDto source, Checkbox target, Question question) {
        var options = new ArrayList<CheckboxOption>();

        for (int i = 0; i < source.getOptions().size(); i++) {
            var op = source.getOptions().get(i);
            var cbOp = new CheckboxOption();

            cbOp.setOption(op.option());
            cbOp.setCheckbox(target);
            cbOp.setOrderIndex(i);

            options.add(cbOp);
        }

        target.setQuestion(question);
        target.setValidationConfig(JsonUtil.objectToOldJsonNode(source.getValidationConfig()));
        target.setOptions(options);
    }

}
