package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TimeResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.TimeResDto;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("TIME_QUESTION_SCHEMA_MANAGER")
public class TimeSchemaManager extends QuestionSchemaManager<TimeResDto, TimeResponseAddReqDto> {

    @Override
    public boolean validateResponse(TimeResponseAddReqDto validationDto, TimeResDto t) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

}
