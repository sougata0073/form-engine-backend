package com.sougata.form_data_service.formValidation.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateTimeResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateTimeResDto;
import com.sougata.form_data_service.formValidation.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("DATE_TIME_QUESTION_SCHEMA_MANAGER")
public class DateTimeSchemaManager extends QuestionSchemaManager<DateTimeResDto, DateTimeResponseAddReqDto> {

    @Override
    public boolean validateResponse(DateTimeResponseAddReqDto validationDto, DateTimeResDto dt) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

}
