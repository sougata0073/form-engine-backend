package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.Rating;
import org.springframework.stereotype.Repository;

@Repository("RATING_REPOSITORY")
public interface RatingRepository extends AnyTypeQuestionRepository<Rating, Long> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

}

