package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.RatingIcon;
import com.sougata.form_service.dto.question.response.RatingResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RatingProjection implements QuestionProjection<RatingResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private Integer maxRatingNumber;
    private RatingIcon ratingIcon;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

    @Override
    public RatingResDto getQuestionResponse() {
        return new RatingResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.RATING,
                maxRatingNumber,
                ratingIcon
        );
    }
}
