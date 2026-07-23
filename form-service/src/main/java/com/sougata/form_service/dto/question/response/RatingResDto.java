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
}
