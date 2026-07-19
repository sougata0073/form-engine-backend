package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.LinearScaleResDto;
import com.sougata.form_data_service.form_schema.model.LinearScaleSchema;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("LINEAR_SCALE_SCHEMA_REPOSITORY")
public interface LinearScaleSchemaRepository extends QuestionSchemaRepository<LinearScaleSchema, Long, LinearScaleResDto> {

    @Query("select l.toNumber from LinearScaleSchema l where l.id = :id")
    Optional<Integer> getToNumber(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

}

