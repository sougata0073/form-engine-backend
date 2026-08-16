package com.sougata.form_service.template.service.questionTemplateManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.template.questionTemplate.DateTimeTemplateDetails;
import com.sougata.form_service.template.model.DateTimeTemplate;
import com.sougata.form_service.template.service.QuestionTemplateManager;
import org.springframework.stereotype.Service;

@Service("DATE_TIME_TEMPLATE_MANAGER")
public class DateTimeTemplateManager extends QuestionTemplateManager<DateTimeTemplate, DateTimeTemplateDetails> {

    @Override
    public DateTimeTemplateDetails toQuestionTemplateDetails(DateTimeTemplate template) {
        var d = new DateTimeTemplateDetails();

        populateCommonFields(template, d);

        return d;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }
}
