package com.sougata.form_service.dto.question.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.model.questionSchema.ShortAnswer;
import com.sougata.form_service.util.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ShortAnswerResDto extends QuestionRes {
    private ValidationConfig validationConfig;

    public ShortAnswerResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, ValidationConfig validationConfig) {
        super(id, question, description, required, orderIndex, questionType);
        this.validationConfig = validationConfig;
    }

    public static ShortAnswerResDto create(ShortAnswer sa) {
        try {
            return new ShortAnswerResDto(
                    sa.getId(),
                    sa.getQuestion(),
                    sa.getDescription(),
                    sa.getRequired(),
                    sa.getOrderIndex(),
                    QuestionType.SHORT_ANSWER,
                    JsonUtil.oldJsonNodeToObject(sa.getValidationConfig(), ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(sa.getValidationConfig()));
        }
    }
}
