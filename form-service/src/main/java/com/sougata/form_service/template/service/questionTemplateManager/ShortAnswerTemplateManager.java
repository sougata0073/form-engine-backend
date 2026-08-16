package com.sougata.form_service.template.service.questionTemplateManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.template.questionTemplate.ShortAnswerTemplateDetails;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.template.model.ShortAnswerTemplate;
import com.sougata.form_service.template.service.QuestionTemplateManager;
import com.sougata.form_service.util.JsonUtil;
import com.sougata.form_service.validation.configuration.ValidationConfig;
import org.springframework.stereotype.Service;

@Service("SHORT_ANSWER_TEMPLATE_MANAGER")
public class ShortAnswerTemplateManager extends QuestionTemplateManager<ShortAnswerTemplate, ShortAnswerTemplateDetails> {

    @Override
    public ShortAnswerTemplateDetails toQuestionTemplateDetails(ShortAnswerTemplate template) {
        var sa = new ShortAnswerTemplateDetails();

        populateCommonFields(template, sa);

        try {
            sa.setValidationConfig(
                    JsonUtil.oldJsonNodeToObject(template.getValidationConfig(), ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(template.getValidationConfig()));
        }

        return sa;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }
}
