package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.CheckboxResDto;
import com.sougata.form_data_service.form_schema.model.CheckboxSchema;
import org.springframework.stereotype.Repository;

@Repository("CHECKBOX_SCHEMA_REPOSITORY")
public interface CheckboxSchemaRepository extends QuestionSchemaRepository<CheckboxSchema, Long, CheckboxResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

}
