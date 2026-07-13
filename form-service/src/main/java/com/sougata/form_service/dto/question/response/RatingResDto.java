package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.RatingIcon;
import com.sougata.form_service.model.questionSchema.Rating;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RatingResDto extends QuestionRes {
    private Integer maxRatingNumber;
    private RatingIcon ratingIcon;

    public RatingResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, Integer maxRatingNumber, RatingIcon ratingIcon) {
        super(id, question, description, required, orderIndex, questionType);
        this.maxRatingNumber = maxRatingNumber;
        this.ratingIcon = ratingIcon;
    }

    public static RatingResDto create(Rating rating) {
        return new RatingResDto(
                rating.getId(),
                rating.getQuestion(),
                rating.getDescription(),
                rating.getRequired(),
                rating.getOrderIndex(),
                QuestionType.RATING,
                rating.getMaxRatingNumber(),
                rating.getRatingIcon()
        );
    }
}
