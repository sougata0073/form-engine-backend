package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.RatingAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.RatingResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.model.questionSchema.Rating;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.repository.RatingRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("RATING_QUESTION_MANAGER")
public class RatingManager extends QuestionManager<Rating, RatingAddUpdateReqDto, RatingResDto> {

    private final RatingRepository ratingRepository;

    public RatingManager(RatingRepository ratingRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.ratingRepository = ratingRepository;
    }

    @Override
    public RatingResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(ratingRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public RatingResDto create(UUID formId, RatingAddUpdateReqDto crudDto) {
        var newR = new Rating();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newR, question);

        var saved = ratingRepository.save(newR);

        return toQuestionResDto(saved);
    }

    @Override
    public RatingResDto create(UUID formId, Long questionId, RatingAddUpdateReqDto crudDto) {
        var newR = new Rating();

        var question = updateQuestion(formId, questionId, crudDto);

        setPropertiesForNew(crudDto, newR, question);

        var saved = ratingRepository.save(newR);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public RatingResDto update(UUID formId, Long questionId, RatingAddUpdateReqDto crudDto) {
        Rating r = ratingRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.RATING, questionId));
        updateQuestion(formId, questionId, crudDto);

        r.setMaxRatingNumber(crudDto.getMaxRatingNumber());
        r.setRatingIcon(crudDto.getRatingIcon());

        ratingRepository.save(r);

        return toQuestionResDto(r);
    }

    @Override
    public RatingResDto toQuestionResDto(Rating question) {
        var r = new RatingResDto();

        populateCommonFields(question, r);

        r.setRatingIcon(question.getRatingIcon());
        r.setMaxRatingNumber(question.getMaxRatingNumber());

        return r;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.RATING;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        ratingRepository.deleteQuestion(formId, questionId);
    }

    private void setPropertiesForNew(RatingAddUpdateReqDto source, Rating target, Question question) {
        target.setQuestion(question);
        target.setMaxRatingNumber(source.getMaxRatingNumber());
        target.setRatingIcon(source.getRatingIcon());
    }
}
