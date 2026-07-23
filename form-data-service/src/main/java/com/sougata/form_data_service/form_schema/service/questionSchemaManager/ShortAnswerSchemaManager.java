package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ShortAnswerResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_data_service.form_schema.responseValidator.ResponseValidatorFactory;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("SHORT_ANSWER_QUESTION_SCHEMA_MANAGER")
public class ShortAnswerSchemaManager extends QuestionSchemaManager<ShortAnswerResDto, ShortAnswerResponseAddReqDto> {

    private final ResponseValidatorFactory responseValidatorFactory;

    public ShortAnswerSchemaManager(ResponseValidatorFactory responseValidatorFactory) {
        this.responseValidatorFactory = responseValidatorFactory;
    }


    @Override
    public boolean validateResponse(ShortAnswerResponseAddReqDto validationDto, ShortAnswerResDto sa) {
        var vConfig = sa.getValidationConfig();

        var validator = responseValidatorFactory.getValidator(vConfig.getValidationId());
        return validator.isValid(validationDto, vConfig);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

}
