package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.constant.ValidationId;
import com.sougata.form_data_service.dto.question.request.ShortAnswerResponseAddReqDto;
import com.sougata.form_data_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_data_service.exception.JsonParsingException;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.ShortAnswerResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.model.ShortAnswerSchema;
import com.sougata.form_data_service.form_schema.repository.ShortAnswerSchemaRepository;
import com.sougata.form_data_service.form_schema.responseValidator.ResponseValidatorFactory;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import com.sougata.form_data_service.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("SHORT_ANSWER_QUESTION_SCHEMA_MANAGER")
public class ShortAnswerSchemaManager extends QuestionSchemaManager<ShortAnswerSchema, ShortAnswerResDto, ShortAnswerResponseAddReqDto> {

    private final ShortAnswerSchemaRepository shortAnswerSchemaRepository;
    private final ResponseValidatorFactory responseValidatorFactory;

    public ShortAnswerSchemaManager(ShortAnswerSchemaRepository shortAnswerSchemaRepository, ResponseValidatorFactory responseValidatorFactory) {
        this.shortAnswerSchemaRepository = shortAnswerSchemaRepository;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public ShortAnswerResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                shortAnswerSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(ShortAnswerResponseAddReqDto validationDto) {
        var vConfig = shortAnswerSchemaRepository.getValidationConfig(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.SHORT_ANSWER, validationDto.getQuestionId()));

        try {
            var validationId = ValidationId.valueOf(
                    JsonUtil.getValueFromOldJsonNode(vConfig, "validationId")
            );

            var validator = responseValidatorFactory.getValidator(validationId);
            var validationConfig = JsonUtil.oldJsonNodeToObject(vConfig, validator.getValidationConfigClass());
            return validator.isValid(validationDto, validationConfig);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(vConfig));
        }
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    public ShortAnswerResDto toQuestionResDto(ShortAnswerSchema questionSchema) {
        var s = new ShortAnswerResDto();

        populateCommonFields(questionSchema, s);

        try {
            s.setValidationConfig(JsonUtil.oldJsonNodeToObject(questionSchema.getValidationConfig(), ValidationConfig.class));
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(questionSchema.getValidationConfig()));
        }

        return s;
    }

}
