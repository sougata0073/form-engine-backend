package com.sougata.form_data_service.form_schema.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.ShortAnswerResDto;
import com.sougata.form_data_service.form_schema.model.ShortAnswerSchema;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("SHORT_ANSWER_SCHEMA_REPOSITORY")
public interface ShortAnswerSchemaRepository extends QuestionSchemaRepository<ShortAnswerSchema, Long, ShortAnswerResDto> {

    @Query("select s.validationConfig from ShortAnswerSchema s where s.id = :id")
    Optional<JsonNode> getValidationConfig(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

}

