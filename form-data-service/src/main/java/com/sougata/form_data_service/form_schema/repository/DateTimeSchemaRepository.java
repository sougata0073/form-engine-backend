package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DateTimeResDto;
import com.sougata.form_data_service.form_schema.model.DateTimeSchema;
import org.springframework.stereotype.Repository;

@Repository("DATE_TIME_SCHEMA_REPOSITORY")
public interface DateTimeSchemaRepository extends QuestionSchemaRepository<DateTimeSchema, Long, DateTimeResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }
}


