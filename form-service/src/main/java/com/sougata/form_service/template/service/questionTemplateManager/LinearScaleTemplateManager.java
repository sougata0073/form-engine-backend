package com.sougata.form_service.template.service.questionTemplateManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.template.questionTemplate.LinearScaleTemplateDetails;
import com.sougata.form_service.template.model.LinearScaleTemplate;
import com.sougata.form_service.template.service.QuestionTemplateManager;
import org.springframework.stereotype.Service;

@Service("LINEAR_SCALE_TEMPLATE_MANAGER")
public class LinearScaleTemplateManager extends QuestionTemplateManager<LinearScaleTemplate, LinearScaleTemplateDetails> {

    @Override
    public LinearScaleTemplateDetails toQuestionTemplateDetails(LinearScaleTemplate template) {
        var ls = new LinearScaleTemplateDetails();

        populateCommonFields(template, ls);

        ls.setFromNumber(template.getFromNumber());
        ls.setToNumber(template.getToNumber());

        return ls;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }
}
