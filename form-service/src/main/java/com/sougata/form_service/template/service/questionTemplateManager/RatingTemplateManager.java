package com.sougata.form_service.template.service.questionTemplateManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.template.questionTemplate.RatingTemplateDetails;
import com.sougata.form_service.template.model.RatingTemplate;
import com.sougata.form_service.template.service.QuestionTemplateManager;
import org.springframework.stereotype.Service;

@Service("RATING_TEMPLATE_MANAGER")
public class RatingTemplateManager extends QuestionTemplateManager<RatingTemplate, RatingTemplateDetails> {

    @Override
    public RatingTemplateDetails toQuestionTemplateDetails(RatingTemplate template) {
        var r = new RatingTemplateDetails();

        populateCommonFields(template, r);

        r.setMaxRatingNumber(template.getMaxRatingNumber());
        r.setRatingIcon(template.getRatingIcon());

        return r;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.RATING;
    }
}
