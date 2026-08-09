package com.sougata.form_data_service.formValidation.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateResDto;
import com.sougata.form_data_service.formValidation.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("DATE_QUESTION_SCHEMA_MANAGER")
public class DateSchemaManager extends QuestionSchemaManager<DateResDto, DateResponseAddReqDto> {


    @Override
    public boolean validateResponse(DateResponseAddReqDto validationDto, DateResDto dt) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

}
