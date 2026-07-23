package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DurationResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DurationResDto;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("DURATION_QUESTION_SCHEMA_MANAGER")
public class DurationSchemaManager extends QuestionSchemaManager<DurationResDto, DurationResponseAddReqDto> {

    @Override
    public boolean validateResponse(DurationResponseAddReqDto validationDto, DurationResDto d) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

}
