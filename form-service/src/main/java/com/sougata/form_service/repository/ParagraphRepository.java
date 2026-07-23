package com.sougata.form_service.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.ParagraphResDto;
import com.sougata.form_service.model.questionSchema.Paragraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("PARAGRAPH_REPOSITORY")
public interface ParagraphRepository extends AnyTypeQuestionRepository<Paragraph, Long, ParagraphResDto> {

    @Query("select p.validationConfig from Paragraph p where p.questionId = :id")
    Optional<JsonNode> getValidationConfig(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

}

