package com.sougata.form_service.template.service.questionTemplateManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.template.questionTemplate.TimeTemplateDetails;
import com.sougata.form_service.template.model.TimeTemplate;
import com.sougata.form_service.template.service.QuestionTemplateManager;
import org.springframework.stereotype.Service;

@Service("TIME_TEMPLATE_MANAGER")
public class TimeTemplateManager extends QuestionTemplateManager<TimeTemplate, TimeTemplateDetails> {

    @Override
    public TimeTemplateDetails toQuestionTemplateDetails(TimeTemplate template) {
        var t = new TimeTemplateDetails();

        populateCommonFields(template, t);

        return t;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }
}
