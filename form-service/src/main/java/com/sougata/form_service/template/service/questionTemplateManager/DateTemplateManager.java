package com.sougata.form_service.template.service.questionTemplateManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.template.questionTemplate.DateTemplateDetails;
import com.sougata.form_service.template.model.DateTemplate;
import com.sougata.form_service.template.service.QuestionTemplateManager;
import org.springframework.stereotype.Service;

@Service("DATE_TEMPLATE_MANAGER")
public class DateTemplateManager extends QuestionTemplateManager<DateTemplate, DateTemplateDetails> {

    @Override
    public DateTemplateDetails toQuestionTemplateDetails(DateTemplate template) {
        var d = new DateTemplateDetails();

        populateCommonFields(template, d);

        return d;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }
}
