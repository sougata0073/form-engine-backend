package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.RatingResDto;
import com.sougata.form_data_service.form_schema.model.RatingSchema;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("RATING_SCHEMA_REPOSITORY")
public interface RatingSchemaRepository extends QuestionSchemaRepository<RatingSchema, Long, RatingResDto> {

    @Query("select r.maxRatingNumber from RatingSchema r where r.id = :id")
    Optional<Integer> getMaxRatingNumber(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

}

