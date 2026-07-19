package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.constant.ValidationId;
import com.sougata.form_data_service.dto.question.request.ParagraphResponseAddReqDto;
import com.sougata.form_data_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_data_service.exception.JsonParsingException;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.ParagraphResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.model.ParagraphSchema;
import com.sougata.form_data_service.form_schema.repository.ParagraphSchemaRepository;
import com.sougata.form_data_service.form_schema.responseValidator.ResponseValidatorFactory;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import com.sougata.form_data_service.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("PARAGRAPH_QUESTION_SCHEMA_MANAGER")
public class ParagraphSchemaManager extends QuestionSchemaManager<ParagraphSchema, ParagraphResDto, ParagraphResponseAddReqDto> {

    private final ParagraphSchemaRepository paragraphSchemaRepository;
    private final ResponseValidatorFactory responseValidatorFactory;

    public ParagraphSchemaManager(ParagraphSchemaRepository paragraphSchemaRepository, ResponseValidatorFactory responseValidatorFactory) {
        this.paragraphSchemaRepository = paragraphSchemaRepository;
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public ParagraphResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                paragraphSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(ParagraphResponseAddReqDto validationDto) {
        var vConfig = paragraphSchemaRepository.getValidationConfig(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.PARAGRAPH, validationDto.getQuestionId()));

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
        return QuestionType.PARAGRAPH;
    }

    @Override
    public ParagraphResDto toQuestionResDto(ParagraphSchema questionSchema) {
        var p = new ParagraphResDto();

        populateCommonFields(questionSchema, p);

        try {
            p.setValidationConfig(JsonUtil.oldJsonNodeToObject(questionSchema.getValidationConfig(), ValidationConfig.class));
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(questionSchema.getValidationConfig()));
        }

        return p;
    }

}
