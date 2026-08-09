package com.sougata.form_data_service.formValidation.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ParagraphResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.ParagraphResDto;
import com.sougata.form_data_service.formValidation.responseValidator.ResponseValidatorFactory;
import com.sougata.form_data_service.formValidation.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("PARAGRAPH_QUESTION_SCHEMA_MANAGER")
public class ParagraphSchemaManager extends QuestionSchemaManager<ParagraphResDto, ParagraphResponseAddReqDto> {

    private final ResponseValidatorFactory responseValidatorFactory;

    public ParagraphSchemaManager(ResponseValidatorFactory responseValidatorFactory) {
        this.responseValidatorFactory = responseValidatorFactory;
    }

    @Override
    public boolean validateResponse(ParagraphResponseAddReqDto validationDto, ParagraphResDto p) {
        var vConfig = p.getValidationConfig();

        var validator = responseValidatorFactory.getValidator(vConfig.getValidationId());
        return validator.isValid(validationDto, vConfig);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

}
