package com.sougata.form_data_service.form_schema.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.ParagraphResDto;
import com.sougata.form_data_service.form_schema.model.ParagraphSchema;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("PARAGRAPH_SCHEMA_REPOSITORY")
public interface ParagraphSchemaRepository extends QuestionSchemaRepository<ParagraphSchema, Long, ParagraphResDto> {

    @Query("select p.validationConfig from ParagraphSchema p where p.id = :id")
    Optional<JsonNode> getValidationConfig(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

}

