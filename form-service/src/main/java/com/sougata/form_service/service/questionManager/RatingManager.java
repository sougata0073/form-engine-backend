package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationMessages;
import com.sougata.form_service.dto.question.request.RatingAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.RatingResDto;
import com.sougata.form_service.dto.validation.request.RatingValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.Rating;
import com.sougata.form_service.repository.RatingRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("RATING_QUESTION_MANAGER")
public class RatingManager extends QuestionManager<RatingAddUpdateReqDto, RatingResDto, RatingValidationRequestDto> {

    private final RatingRepository ratingRepository;
    private final FormService formService;

    public RatingManager(RatingRepository ratingRepository, FormService formService) {
        this.ratingRepository = ratingRepository;
        this.formService = formService;
    }

    @Override
    public RatingResDto create(UUID formId, RatingAddUpdateReqDto crudDto) {
        Rating newR = new Rating();

        setProperties(crudDto, formId, newR);

        Rating saved = ratingRepository.save(newR);

        return RatingResDto.create(saved);
    }

    @Override
    public RatingResDto create(UUID formId, Long questionId, RatingAddUpdateReqDto crudDto) {
        Rating newR = new Rating();

        newR.setId(questionId);
        setProperties(crudDto, formId, newR);

        Rating saved = ratingRepository.save(newR);

        return RatingResDto.create(saved);
    }

    @Override
    public RatingResDto update(Long questionId, RatingAddUpdateReqDto crudDto) {
        Rating r = ratingRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.RATING, questionId));
        setProperties(crudDto, r);
        ratingRepository.save(r);

        return RatingResDto.create(r);
    }

    @Override
    public boolean exists(Long questionId) {
        return ratingRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        ratingRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(RatingValidationRequestDto validationDto) {
        Rating rating = ratingRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.RATING, validationDto.getQuestionId()));

        if (validationDto.getRating() > rating.getMaxRatingNumber()) {
            throw new ResponseValidationException(String.format(ValidationMessages.INVALID_RATING_NUMBER, rating.getMaxRatingNumber()));
        }

        return true;
    }

    @Override
    public Class<RatingAddUpdateReqDto> getCrudDtoClass() {
        return RatingAddUpdateReqDto.class;
    }

    @Override
    public Class<RatingValidationRequestDto> getValidationDtoClass() {
        return RatingValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RatingRepository getQuestionRepository() {
        return ratingRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

    private void setProperties(RatingAddUpdateReqDto source, UUID formId, Rating target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setMaxRatingNumber(source.getMaxRatingNumber());
        target.setRatingIcon(source.getRatingIcon());
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(RatingAddUpdateReqDto source, Rating target) {
        setProperties(source, null, target);
    }
}
