package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.RatingResDto;
import com.sougata.form_service.model.Rating;
import org.springframework.stereotype.Repository;

@Repository("RATING_REPOSITORY")
public interface RatingRepository extends QuestionRepository<Rating, Long, RatingResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

    @Override
    default RatingResDto toQuestionResDto(Rating rating) {
        return RatingResDto.create(rating);
    }
}

