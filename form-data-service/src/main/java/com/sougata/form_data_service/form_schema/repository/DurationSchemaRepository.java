package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DurationResDto;
import com.sougata.form_data_service.form_schema.model.DurationSchema;
import org.springframework.stereotype.Repository;

@Repository("DURATION_SCHEMA_REPOSITORY")
public interface DurationSchemaRepository extends QuestionSchemaRepository<DurationSchema, Long, DurationResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

}

