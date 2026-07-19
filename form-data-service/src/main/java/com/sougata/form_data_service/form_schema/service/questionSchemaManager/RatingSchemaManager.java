package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.constant.ValidationMessages;
import com.sougata.form_data_service.dto.question.request.RatingResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.RatingResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.RatingSchema;
import com.sougata.form_data_service.form_schema.repository.RatingSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("RATING_QUESTION_SCHEMA_MANAGER")
public class RatingSchemaManager extends QuestionSchemaManager<RatingSchema, RatingResDto, RatingResponseAddReqDto> {

    private final RatingSchemaRepository ratingSchemaRepository;

    public RatingSchemaManager(RatingSchemaRepository ratingSchemaRepository) {
        this.ratingSchemaRepository = ratingSchemaRepository;
    }

    @Override
    public RatingResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                ratingSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(RatingResponseAddReqDto validationDto) {
        var maxRatingNumber = ratingSchemaRepository.getMaxRatingNumber(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.RATING, validationDto.getQuestionId()));

        if (validationDto.getRating() > maxRatingNumber) {
            throw new ResponseValidationException(String.format(ValidationMessages.INVALID_RATING_NUMBER, maxRatingNumber));
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

    @Override
    public RatingResDto toQuestionResDto(RatingSchema questionSchema) {
        var r = new RatingResDto();

        populateCommonFields(questionSchema, r);

        r.setRatingIcon(questionSchema.getRatingIcon());
        r.setMaxRatingNumber(questionSchema.getMaxRatingNumber());

        return r;
    }

}
