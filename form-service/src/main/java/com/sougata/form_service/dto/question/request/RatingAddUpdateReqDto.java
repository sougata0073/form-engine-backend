package com.sougata.form_service.dto.question.request;

import com.sougata.form_service.constant.RatingIcon;
import com.sougata.form_service.constant.ValidationMessages;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RatingAddUpdateReqDto extends QuestionAddUpdateReq {

    @NotNull(message = ValidationMessages.MAX_RATING_NUMBER_NOT_NULL)
    @Max(value = 20, message = ValidationMessages.INVALID_MAX_RATING_RANGE)
    private Integer maxRatingNumber;

    @NotNull(message = ValidationMessages.RATING_ICON_NOT_NULL)
    private RatingIcon ratingIcon;
}
