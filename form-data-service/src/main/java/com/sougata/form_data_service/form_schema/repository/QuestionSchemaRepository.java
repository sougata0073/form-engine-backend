package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.QuestionRes;
import com.sougata.form_data_service.form_schema.model.QuestionSchema;
import com.sougata.form_data_service.form_schema.projection.QuestionIdProjection;
import com.sougata.form_data_service.form_schema.projection.QuestionSchemaSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionSchemaRepository<Q extends QuestionSchema, ID, QRD extends QuestionRes> extends JpaRepository<Q, ID> {

    List<Q> findByFormId(UUID id);

    Optional<Q> findByFormIdAndId(UUID formId, Long questionId);

    List<QuestionIdProjection> findByFormIdAndRequired(UUID formId, Boolean required);

    List<QuestionSchemaSummaryProjection> findQuestionSummariesByFormId(UUID formId);

    Optional<QuestionSchemaSummaryProjection> findQuestionSummaryByFormIdAndId(UUID formId, Long questionId);

    default QuestionType getQuestionType() {
        throw new UnsupportedOperationException(
                "Must be implemented by concrete repository"
        );
    }
}
