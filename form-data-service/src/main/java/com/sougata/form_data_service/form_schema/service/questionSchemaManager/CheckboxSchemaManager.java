package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.constant.ValidationId;
import com.sougata.form_data_service.dto.question.request.CheckboxResponseAddReqDto;
import com.sougata.form_data_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_data_service.exception.JsonParsingException;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.CheckboxResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.CheckboxOptionSchema;
import com.sougata.form_data_service.form_schema.model.CheckboxSchema;
import com.sougata.form_data_service.form_schema.repository.CheckboxSchemaRepository;
import com.sougata.form_data_service.form_schema.responseValidator.ResponseValidatorFactory;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import com.sougata.form_data_service.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@Service("CHECKBOX_QUESTION_SCHEMA_MANAGER")
public class CheckboxSchemaManager extends QuestionSchemaManager<CheckboxSchema, CheckboxResDto, CheckboxResponseAddReqDto> {

    private final CheckboxSchemaRepository checkboxSchemaRepository;
    private final ResponseValidatorFactory responseValidatorFactory;

    @Autowired
    public CheckboxSchemaManager(CheckboxSchemaRepository checkboxSchemaRepository, ResponseValidatorFactory responseValidatorFactory) {
        this.checkboxSchemaRepository = checkboxSchemaRepository;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public CheckboxResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                checkboxSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(CheckboxResponseAddReqDto validationDto) {
        CheckboxSchema cb = checkboxSchemaRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.CHECKBOX, validationDto.getQuestionId()));

        var optionIdSet = new HashSet<>(cb.getOptions().stream().map(CheckboxOptionSchema::getId).toList());
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
    public CheckboxResDto toQuestionResDto(CheckboxSchema questionSchema) {
        var cb = new CheckboxResDto();

        populateCommonFields(questionSchema, cb);

        cb.setOptions(
                questionSchema.getOptions().stream()
                        .map(o ->
                                new CheckboxResDto.CheckboxOptionResDto(o.getId(), o.getOption(), o.getOrderIndex())
                        )
                        .toList()
        );

        try {
            cb.setValidationConfig(
                    JsonUtil.oldJsonNodeToObject(questionSchema.getValidationConfig(), ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(questionSchema.getValidationConfig()));
        }

        return cb;
    }


}
