package com.sougata.form_service.template.service.questionTemplateManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.template.questionTemplate.ParagraphTemplateDetails;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.template.model.ParagraphTemplate;
import com.sougata.form_service.template.service.QuestionTemplateManager;
import com.sougata.form_service.util.JsonUtil;
import com.sougata.form_service.validation.configuration.ValidationConfig;
import org.springframework.stereotype.Service;

@Service("PARAGRAPH_TEMPLATE_MANAGER")
public class ParagraphTemplateManager extends QuestionTemplateManager<ParagraphTemplate, ParagraphTemplateDetails> {

    @Override
    public ParagraphTemplateDetails toQuestionTemplateDetails(ParagraphTemplate template) {
        var p = new ParagraphTemplateDetails();

        populateCommonFields(template, p);

        try {
            p.setValidationConfig(
                    JsonUtil.oldJsonNodeToObject(template.getValidationConfig(), ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(template.getValidationConfig()));
        }

        return p;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }
}
