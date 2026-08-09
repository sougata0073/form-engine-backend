package com.sougata.form_data_service.formValidation.service.questionSchemaManager;

import com.sougata.form_data_service.constant.ExceptionMessages;
import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.LinearScaleResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_data_service.formValidation.exception.ResponseValidationException;
import com.sougata.form_data_service.formValidation.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("LINEAR_SCALE_QUESTION_SCHEMA_MANAGER")
public class LinearScaleSchemaManager extends QuestionSchemaManager<LinearScaleResDto, LinearScaleResponseAddReqDto> {

    @Override
    public boolean validateResponse(LinearScaleResponseAddReqDto validationDto, LinearScaleResDto ls) {
        Integer toNumber = ls.getToNumber();

        if (validationDto.getScale() > toNumber) {
            throw new ResponseValidationException(
                    String.format(
                            ExceptionMessages.INVALID_SCALE, toNumber, validationDto.getScale()
                    )
            );
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

}
