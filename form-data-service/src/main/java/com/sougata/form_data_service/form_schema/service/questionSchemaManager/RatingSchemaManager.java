package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.constant.ValidationMessages;
import com.sougata.form_data_service.dto.question.request.RatingResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.RatingResDto;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

@Service("RATING_QUESTION_SCHEMA_MANAGER")
public class RatingSchemaManager extends QuestionSchemaManager<RatingResDto, RatingResponseAddReqDto> {

    @Override
    public boolean validateResponse(RatingResponseAddReqDto validationDto, RatingResDto rt) {
        var maxRatingNumber = rt.getMaxRatingNumber();

        if (validationDto.getRating() > maxRatingNumber) {
            throw new ResponseValidationException(String.format(ValidationMessages.INVALID_RATING_NUMBER, maxRatingNumber));
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

}
