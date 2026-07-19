package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.TickBoxGridResDto;
import com.sougata.form_data_service.form_schema.model.TickBoxGridSchema;
import org.springframework.stereotype.Repository;

@Repository("TICK_BOX_GRID_SCHEMA_REPOSITORY")
public interface TickBoxGridSchemaRepository extends QuestionSchemaRepository<TickBoxGridSchema, Long, TickBoxGridResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

}

