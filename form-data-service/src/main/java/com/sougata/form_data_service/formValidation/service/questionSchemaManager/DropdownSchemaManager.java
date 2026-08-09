package com.sougata.form_data_service.formValidation.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DropdownResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DropdownResDto;
import com.sougata.form_data_service.formValidation.exception.ResponseValidationException;
import com.sougata.form_data_service.formValidation.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("DROPDOWN_QUESTION_SCHEMA_MANAGER")
public class DropdownSchemaManager extends QuestionSchemaManager<DropdownResDto, DropdownResponseAddReqDto> {

    @Override
    public boolean validateResponse(DropdownResponseAddReqDto validationDto, DropdownResDto dd) {
        var present = dd.getOptions().stream()
                .anyMatch(op -> Objects.equals(op.id(), validationDto.getResponseOptionId()));

        if (!present) {
            throw new ResponseValidationException(
                    "Invalid dropdown option ID: " + validationDto.getResponseOptionId()
            );
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

}
