package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.MultipleChoiceResDto;
import com.sougata.form_data_service.form_schema.model.MultipleChoiceSchema;
import org.springframework.stereotype.Repository;

@Repository("MULTIPLE_CHOICE_SCHEMA_REPOSITORY")
public interface MultipleChoiceSchemaRepository extends QuestionSchemaRepository<MultipleChoiceSchema, Long, MultipleChoiceResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

}

