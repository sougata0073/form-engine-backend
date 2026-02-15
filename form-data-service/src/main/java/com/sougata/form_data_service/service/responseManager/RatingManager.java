package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.RatingResponseAddReqDto;
import com.sougata.form_data_service.model.Rating;
import com.sougata.form_data_service.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("RATING_RESPONSE_MANAGER")
public class RatingManager extends ResponseManager<RatingResponseAddReqDto> {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingManager(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public void create(RatingResponseAddReqDto response) {
        Rating rating = new Rating();
        rating.setRating(response.getRating());
        rating.setQuestionId(response.getQuestionId());

        ratingRepository.save(rating);
    }
}
