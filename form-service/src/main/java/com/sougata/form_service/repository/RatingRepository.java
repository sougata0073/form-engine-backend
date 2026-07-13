package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.RatingResDto;
import com.sougata.form_service.model.questionSchema.Rating;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("RATING_REPOSITORY")
public interface RatingRepository extends QuestionRepository<Rating, Long, RatingResDto> {

    @Query("select r.maxRatingNumber from Rating r where r.id = :id")
    Optional<Integer> getMaxRatingNumber(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

    @Override
    default RatingResDto toQuestionResDto(Rating rating) {
        return RatingResDto.create(rating);
    }
}

