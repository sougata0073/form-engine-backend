package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DateResDto;
import com.sougata.form_data_service.form_schema.model.DateSchema;
import org.springframework.stereotype.Repository;

@Repository("DATE_SCHEMA_REPOSITORY")
public interface DateSchemaRepository extends QuestionSchemaRepository<DateSchema, Long, DateResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

}

