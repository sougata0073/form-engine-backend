package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.TimeResDto;
import com.sougata.form_data_service.form_schema.model.TimeSchema;
import org.springframework.stereotype.Repository;

@Repository("TIME_SCHEMA_REPOSITORY")
public interface TimeSchemaRepository extends QuestionSchemaRepository<TimeSchema, Long, TimeResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

}