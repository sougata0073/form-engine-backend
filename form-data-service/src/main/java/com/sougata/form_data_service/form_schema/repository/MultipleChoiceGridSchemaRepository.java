package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.MultipleChoiceGridResDto;
import com.sougata.form_data_service.form_schema.model.MultipleChoiceGridSchema;
import org.springframework.stereotype.Repository;

@Repository("MULTIPLE_CHOICE_GRID_SCHEMA_REPOSITORY")
public interface MultipleChoiceGridSchemaRepository extends QuestionSchemaRepository<MultipleChoiceGridSchema, Long, MultipleChoiceGridResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

}

