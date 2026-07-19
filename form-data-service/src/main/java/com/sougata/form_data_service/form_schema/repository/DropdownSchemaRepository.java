package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DropdownResDto;
import com.sougata.form_data_service.form_schema.model.DropdownSchema;
import org.springframework.stereotype.Repository;

@Repository("DROPDOWN_SCHEMA_REPOSITORY")
public interface DropdownSchemaRepository extends QuestionSchemaRepository<DropdownSchema, Long, DropdownResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

}
